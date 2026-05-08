package com.bantads.conta.service;

import com.bantads.conta.dto.DepositoDTO;
import com.bantads.conta.dto.ExtratoResponseDTO;
import com.bantads.conta.dto.MovimentacaoDTO;
import com.bantads.conta.dto.SaqueDTO;
import com.bantads.conta.dto.TransferenciaDTO;
import com.bantads.conta.model.Movimentacao;
import com.bantads.conta.model.TipoMovimentacao;
import com.bantads.conta.repository.ContaRepository;
import com.bantads.conta.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MovimentacaoService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    public ExtratoResponseDTO getExtrato(String numConta, LocalDate inicio, LocalDate fim) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.atTime(LocalTime.MAX);

        // 1. Buscar todas as movimentações antes do período para calcular o saldo inicial
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

        // 2. Buscar as movimentações do período
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
                    saldoAtual = saldoAtual.subtract(valor);
                } else {
                    saldoAtual = saldoAtual.add(valor);
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
            saldosDiarios.put(dataCorrente.toString(), saldoAtual);
            dataCorrente = dataCorrente.plusDays(1);
        }

        return new ExtratoResponseDTO(dtos, saldosDiarios);
    }

    @Transactional
    public void depositar(DepositoDTO dto) {
        var conta = contaRepository.findByConta(dto.numeroConta())
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        conta.setSaldo(conta.getSaldo().add(dto.valor()));
        contaRepository.save(conta);

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now())
                .tipo(TipoMovimentacao.DEPOSITO)
                .valor(dto.valor())
                .contaOrigem(dto.numeroConta())
                .build();

        movimentacaoRepository.save(movimentacao);

    }

    @Transactional
    public void sacar(SaqueDTO dto) {
        var conta = contaRepository.findByConta(dto.numeroConta())
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        if (conta.getSaldo().add(conta.getLimite()).compareTo(dto.valor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(dto.valor()));
        contaRepository.save(conta);

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now())
                .tipo(TipoMovimentacao.SAQUE)
                .valor(dto.valor())
                .contaOrigem(dto.numeroConta())
                .build();

        movimentacaoRepository.save(movimentacao);
    }

    @Transactional
    public void transferir(TransferenciaDTO dto) {
        var origem = contaRepository.findByConta(dto.numeroContaOrigem())
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));
        var destino = contaRepository.findByConta(dto.numeroContaDestino())
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));

        if (origem.getSaldo().add(origem.getLimite()).compareTo(dto.valor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        origem.setSaldo(origem.getSaldo().subtract(dto.valor()));
        destino.setSaldo(destino.getSaldo().add(dto.valor()));

        contaRepository.save(origem);
        contaRepository.save(destino);

        var movimentacao = Movimentacao.builder()
                .dataHora(LocalDateTime.now())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .valor(dto.valor())
                .contaOrigem(dto.numeroContaOrigem())
                .contaDestino(dto.numeroContaDestino())
                .build();

        movimentacaoRepository.save(movimentacao);
    }
}
