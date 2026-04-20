package com.bantads.conta.service;

import com.bantads.conta.dto.ContaCreateInputDTO;
import com.bantads.conta.dto.ContaCreateOutputDTO;
import com.bantads.conta.model.Conta;
import com.bantads.conta.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public ContaCreateOutputDTO createConta(ContaCreateInputDTO dto) throws Exception {

        if(contaRepository.existsByCpf(dto.cpf())) {
            throw new IllegalStateException("CPF já cadastrado");
        }

        var numConta = ThreadLocalRandom.current().nextInt(1000, 9999)+"";

        var limite = dto.salario().divide(new BigDecimal(2), RoundingMode.UNNECESSARY);

        Conta conta = new Conta(
                LocalDateTime.now(),
                limite,
                BigDecimal.ZERO,
                numConta,
                dto.cpf()
                );
        contaRepository.save(conta);

        return new ContaCreateOutputDTO(dto.cpf(), numConta, new BigDecimal(0), limite);
    }

}
