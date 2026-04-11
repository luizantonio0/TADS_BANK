package com.bantads.conta.service;

import com.bantads.conta.dto.ContaCreateDTO;
import com.bantads.conta.model.Conta;
import com.bantads.conta.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public void createConta(ContaCreateDTO dto) throws Exception {
        if(contaRepository.existsByCpf(dto.cpf())) {
            throw new IllegalStateException("CPF já cadastrado");
        }
        if(contaRepository.existsByConta(dto.numConta())) {
            throw new IllegalStateException("Número de conta já cadastrado");
        }
        Conta conta = new Conta(
                (Date) Date.from(Instant.now()),
                dto.limite(),
                dto.saldo(),
                dto.numConta(),
                dto.cpf()
                );
        contaRepository.save(conta);
    }

}
