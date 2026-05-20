package com.bantads.conta.service;

import com.bantads.conta.dto.ContaCreateInputDTO;
import com.bantads.conta.model.Conta;
import com.bantads.conta.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public void rollbackConta(UUID uuid) throws Exception {
        Page<Revision<Integer, Conta>> revisions = contaRepository.findRevisions(uuid, PageRequest.of(0, 2));
        List<Revision<Integer, Conta>> content = revisions.getContent();

        if (content.size() >= 2) {
            var revision = content.get(1).getEntity();
            contaRepository.save(revision);
        } else {
            var whitelist = List.of("1291", "0950", "8573", "5887", "7617");
            var conta = contaRepository.findById(uuid);
            if(conta.isPresent() && !whitelist.contains(conta.get().getConta()))
                contaRepository.deleteById(uuid);
        }

    }

    public Conta getConta(String numConta) {
        return contaRepository.findByConta(numConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }

    public Conta atualizarLimite(String cpf, BigDecimal salario) {
        var optConta = contaRepository.findByConta(cpf);
        if (optConta.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        var conta = optConta.get();
        conta.setLimite(salario.divide(new BigDecimal(2), RoundingMode.UNNECESSARY));
        contaRepository.save(conta);
        return conta;
    }

    public Conta createConta(ContaCreateInputDTO dto) throws Exception {

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
        return conta;
    }

}
