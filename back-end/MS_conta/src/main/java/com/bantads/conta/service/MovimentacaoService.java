package com.bantads.conta.service;

import com.bantads.conta.dto.DepositoDTO;
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
}
