package com.bantads.conta.service;

import com.bantads.conta.dto.DepositoDTO;
import com.bantads.conta.dto.SaqueDTO;
import com.bantads.conta.dto.TransferenciaDTO;
import com.bantads.conta.model.Movimentacao;
import com.bantads.conta.model.TipoMovimentacao;
import com.bantads.conta.repository.ContaRepository;
import com.bantads.conta.repository.MovimentacaoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MovimentacaoService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
