package com.bantads.conta.service;

import com.bantads.conta.datasource.DataSourceContextHolder;
import com.bantads.conta.datasource.DataSourceType;
import com.bantads.conta.dto.*;
import com.bantads.conta.dto.cqrs.CQRSSyncEntity;
import com.bantads.conta.exception.BadRequestException;
import com.bantads.conta.exception.ForbiddenException;
import com.bantads.conta.exception.HttpException;
import com.bantads.conta.model.Conta;
import com.bantads.conta.model.Movimentacao;
import com.bantads.conta.model.TipoMovimentacao;
import com.bantads.conta.repository.read.MovimentacaoReadRepository;
import com.bantads.conta.repository.write.ContaRepository;
import com.bantads.conta.repository.write.MovimentacaoRepository;

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
    @Autowired private MovimentacaoReadRepository readRepository;
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
    public void sacar(String conta, SaqueDTO dto) {
        var valor = dto.valor().setScale(2, RoundingMode.HALF_UP);
        var contaDestino = contaRepository.findByConta(conta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        BigDecimal saldoDisponivel = contaDestino.getSaldo().add(contaDestino.getLimite());
        if (saldoDisponivel.compareTo(valor) < 0) {
            throw new IllegalStateException("Saldo insuficiente (considerando limite)");
        }

        contaDestino.setSaldo(contaDestino.getSaldo().subtract(valor).setScale(2, RoundingMode.HALF_UP));

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now())
                .tipo(TipoMovimentacao.SAQUE)
                .valor(valor)
                .contaOrigem(conta)
                .build();

        movimentacaoRepository.save(movimentacao);
        contaRepository.save(contaDestino);

        sincronizarMovimentacao(movimentacao);
        sincronizarConta(contaDestino);
    }

    @Transactional
    public void transferir(String conta, String cpfLogado, TransferenciaDTO dto) throws HttpException {
        var valor = dto.valor().setScale(2, RoundingMode.HALF_UP);
        var origem = contaRepository.findByConta(conta)
                .orElseThrow(() -> new BadRequestException("Conta de origem não encontrada"));

        if (!origem.getCpf().equals(cpfLogado)) {
            throw new ForbiddenException("Você não tem permissão para realizar essa operação");
        }

        var destino = contaRepository.findByConta(dto.destino())
                .orElseThrow(() -> new BadRequestException("Conta de destino não encontrada"));

        if (destino.getCpf().equals(origem.getCpf())) {
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
        var dto = CQRSSyncEntity.MovimentacaoDTO.from(movimentacao);
        rabbitTemplate.convertAndSend("ms-conta.cqrs.movimentacao", dto);
    }

    protected void sincronizarConta(Conta conta) {
        var dto = CQRSSyncEntity.ContaDTO.from(conta);
        rabbitTemplate.convertAndSend("ms-conta.cqrs.conta", dto);
    }

    @Transactional(readOnly = true)
    public ExtratoResponseDTO getExtrato(String numConta, LocalDate inicio, LocalDate fim) throws BadRequestException {

        var primeiraMovimentacaoOpt = movimentacaoRepository.findFirstByContaOrigemOrContaDestinoOrderByDataHoraAsc(numConta, numConta);
        var ultimaMovimentacaoOpt = movimentacaoRepository.findFirstByContaOrigemOrContaDestinoOrderByDataHoraDesc(numConta, numConta);

        Optional<Conta> contaOpt = contaRepository.findByConta(numConta);
        if(contaOpt.isEmpty()) {
            throw new BadRequestException("Conta não encontrada");
        }

        if (primeiraMovimentacaoOpt.isEmpty()) {
            return new ExtratoResponseDTO(numConta, BigDecimal.ZERO, List.of(), Map.of());
        }

        var primeiraMovimentacao = primeiraMovimentacaoOpt.get();
        var ultimaMovimentacao = ultimaMovimentacaoOpt.get();
        var conta = contaOpt.get();

        LocalDateTime dataInicio = inicio == null ? LocalDateTime.MIN : inicio.isBefore(primeiraMovimentacao.getDataHora().toLocalDate()) 
            ? primeiraMovimentacao.getDataHora().toLocalDate().atStartOfDay() : inicio.atStartOfDay();
        LocalDateTime dataFim = fim == null ? LocalDateTime.MAX : fim.isAfter(ultimaMovimentacao.getDataHora().toLocalDate()) 
            ? ultimaMovimentacao.getDataHora().toLocalDate().atTime(LocalTime.MAX) : fim.atTime(LocalTime.MAX);

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

        LocalDate dataCorrente = dataInicio.toLocalDate();
        int indexMov = 0;

        while (!dataCorrente.isAfter(dataFim.toLocalDate())) {
            while (indexMov < periodo.size() && periodo.get(indexMov).getDataHora().toLocalDate().equals(dataCorrente)) {
                Movimentacao m = periodo.get(indexMov);
                BigDecimal valor = m.getValor();

                if (m.getTipo() == TipoMovimentacao.SAQUE) {
                    saldoAtual = saldoAtual.subtract(valor);
                } else if (m.getTipo() == TipoMovimentacao.DEPOSITO) {
                    saldoAtual = saldoAtual.add(valor);
                } else if (m.getTipo() == TipoMovimentacao.TRANSFERENCIA) {
                    if (numConta.equals(m.getContaOrigem())) {
                        saldoAtual = saldoAtual.subtract(valor);
                    } else {
                        saldoAtual = saldoAtual.add(valor);
                    }
                }

                dtos.add(new MovimentacaoDTO(
                    m.getDataHora(),
                    m.getTipo().name(),
                    m.getContaOrigem(),
                    m.getContaDestino(),
                    valor
                ));
                indexMov++;
            }
            saldosDiarios.put(dataCorrente.toString(), saldoAtual.setScale(2, RoundingMode.HALF_UP));
            dataCorrente = dataCorrente.plusDays(1);
        }

        return new ExtratoResponseDTO(conta.getConta(), conta.getSaldo(), dtos, saldosDiarios);
    }

    public void sync(CQRSSyncEntity.MovimentacaoDTO m) {
        DataSourceContextHolder.setContext(DataSourceType.READER);
        Movimentacao c = new Movimentacao();
        c.setContaDestino(m.contaDestino());
        c.setContaOrigem(m.contaOrigem());
        c.setDataHora(m.dataHora());
        c.setId(m.id());
        c.setTipo(TipoMovimentacao.valueOf(m.tipo()));
        c.setValor(m.valor());
        readRepository.save(c);
    }
}
