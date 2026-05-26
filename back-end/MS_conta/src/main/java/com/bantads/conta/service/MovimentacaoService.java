package com.bantads.conta.service;

import com.bantads.conta.datasource.DataSourceContextHolder;
import com.bantads.conta.datasource.DataSourceType;
import com.bantads.conta.dto.*;
import com.bantads.conta.exception.BadRequestException;
import com.bantads.conta.exception.ForbiddenException;
import com.bantads.conta.exception.HttpException;
import com.bantads.conta.model.Conta;
import com.bantads.conta.model.Movimentacao;
import com.bantads.conta.model.TipoMovimentacao;
import com.bantads.conta.repository.ContaReadRepository;
import com.bantads.conta.repository.ContaRepository;
import com.bantads.conta.repository.MovimentacaoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MovimentacaoService {

    @Autowired private ContaRepository contaRepository;
    @Autowired private MovimentacaoRepository movimentacaoRepository;
    @Autowired private RabbitTemplate rabbitTemplate;

    @Transactional
    public void depositar(String conta, String cpfLogado, DepositoDTO dto) throws HttpException {
        var valor = dto.valor().setScale(2, RoundingMode.HALF_UP);
        var contaDestino = contaRepository.findByConta(conta)
            .orElseThrow(() -> new BadRequestException("Conta de depósito não encontrada"));

        if (!contaDestino.getCpf().equals(cpfLogado)) {
            throw new ForbiddenException("Você não tem permissão para realizar essa operação");
        }

        contaDestino.setSaldo(contaDestino.getSaldo().add(valor).setScale(2, RoundingMode.HALF_UP));
        contaRepository.save(contaDestino);

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now())
                .tipo(TipoMovimentacao.DEPOSITO)
                .valor(valor)
                .contaOrigem(conta)
                .build();

        movimentacaoRepository.save(movimentacao);
        sincronizarMovimentacao(movimentacao);
        sincronizarConta(contaDestino);
    }

    @Transactional
    public void sacar(SaqueDTO dto) {
        var valor = dto.valor().setScale(2, RoundingMode.HALF_UP);
        var conta = contaRepository.findByConta(dto.numeroConta())
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        BigDecimal saldoDisponivel = conta.getSaldo().add(conta.getLimite());
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new IllegalStateException("Saldo insuficiente (considerando limite)");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor).setScale(2, RoundingMode.HALF_UP));

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now())
                .tipo(TipoMovimentacao.SAQUE)
                .valor(valor)
                .contaOrigem(dto.numeroConta())
                .build();

        sincronizarMovimentacao(movimentacao);
        sincronizarConta(conta);

        movimentacaoRepository.save(movimentacao);
        contaRepository.save(conta);
    }

    @Transactional
    public void transferir(String conta, String cpfLogado, TransferenciaDTO dto) throws HttpException {
        var valor = dto.valor().setScale(2, RoundingMode.HALF_UP);
        var origem = contaRepository.findByConta(conta)
                .orElseThrow(() -> new BadRequestException("Conta de origem não encontrada"));

        if(!origem.getCpf().equals(cpfLogado)) {
            throw new ForbiddenException("Você não tem permissão para realizar essa operação");
        }
            
        var destino = contaRepository.findByConta(dto.destino())
                .orElseThrow(() -> new BadRequestException("Conta de destino não encontrada"));

        if(destino.getCpf().equals(origem.getCpf())) {
            throw new ForbiddenException("Não é permitido transferir para a própria conta");
        }

        BigDecimal saldoDisponivel = origem.getSaldo().add(origem.getLimite());
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new BadRequestException("Saldo insuficiente na conta de origem");
        }

        origem.setSaldo(origem.getSaldo().subtract(valor).setScale(2, RoundingMode.HALF_UP));
        destino.setSaldo(destino.getSaldo().add(valor).setScale(2, RoundingMode.HALF_UP));

        contaRepository.save(origem);
        contaRepository.save(destino);

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .valor(valor)
                .contaOrigem(conta)
                .contaDestino(dto.destino())
                .build();

        movimentacaoRepository.save(movimentacao);

        sincronizarConta(origem);
        sincronizarConta(destino);
        sincronizarMovimentacao(movimentacao);
    }

    protected void sincronizarMovimentacao(Movimentacao movimentacao) {
        rabbitTemplate.convertAndSend("ms-conta.cqrs.movimentacao", movimentacao);
    }

    protected void sincronizarConta(Conta conta) {
        rabbitTemplate.convertAndSend("ms-conta.cqrs.conta", conta);
    }

    @Transactional(readOnly = true)
    public ExtratoResponseDTO getExtrato(String numConta, LocalDate inicio, LocalDate fim) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.atTime(LocalTime.MAX);

        List<Movimentacao> anteriores = movimentacaoRepository.findByContaBefore(numConta, dataInicio);
        BigDecimal saldoAtual = BigDecimal.ZERO;

        for (Movimentacao m : anteriores) {
            if (m.getTipo() == TipoMovimentacao.DEPOSITO) {
                saldoAtual = saldoAtual.add(m.getValor());
            } else if (m.getTipo() == TipoMovimentacao.SAQUE) {
                saldoAtual = saldoAtual.subtract(m.getValor());
            } else if (m.getTipo() == TipoMovimentacao.TRANSFERENCIA) {
                if (numConta.equals(m.getContaOrigem())) {
                    saldoAtual = saldoAtual.subtract(m.getValor());
                } else {
                    saldoAtual = saldoAtual.add(m.getValor());
                }
            }
        }

        List<Movimentacao> periodo = movimentacaoRepository.findByContaAndPeriodo(numConta, dataInicio, dataFim);
        List<MovimentacaoDTO> dtos = new ArrayList<>();
        Map<String, BigDecimal> saldosDiarios = new LinkedHashMap<>();

        LocalDate dataCorrente = inicio;
        int indexMov = 0;

        while (!dataCorrente.isAfter(fim)) {
            while (indexMov < periodo.size() && periodo.get(indexMov).getDataHora().toLocalDate().equals(dataCorrente)) {
                Movimentacao m = periodo.get(indexMov);
                String cor = "azul";
                BigDecimal valor = m.getValor();

                if (m.getTipo() == TipoMovimentacao.SAQUE || (m.getTipo() == TipoMovimentacao.TRANSFERENCIA && numConta.equals(m.getContaOrigem()))) {
                    cor = "vermelho";
                }

                dtos.add(new MovimentacaoDTO(
                    m.getDataHora(),
                    m.getTipo().name(),
                    m.getContaOrigem(),
                    m.getContaDestino(),
                    valor,
                    cor
                ));
                indexMov++;
            }
            
            saldosDiarios.put(dataCorrente.toString(), saldoAtual.setScale(2, RoundingMode.HALF_UP));
            dataCorrente = dataCorrente.plusDays(1);
        }

        return new ExtratoResponseDTO(dtos, saldosDiarios);
    }

    public void sync(Movimentacao conta) {
        DataSourceContextHolder.setContext(DataSourceType.READER);
        movimentacaoRepository.save(conta);
    }
}
