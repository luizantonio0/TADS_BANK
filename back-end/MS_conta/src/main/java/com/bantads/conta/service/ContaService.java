package com.bantads.conta.service;

import com.bantads.conta.datasource.DataSourceContextHolder;
import com.bantads.conta.datasource.DataSourceType;
import com.bantads.conta.dto.ContaCreateInputDTO;
import com.bantads.conta.dto.ContaDTO;
import com.bantads.conta.dto.SaldoGerenteDTO;
import com.bantads.conta.exception.NotFoundException;
import com.bantads.conta.model.Conta;
import com.bantads.conta.model.Movimentacao;
import com.bantads.conta.repository.ContaRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void rollbackConta(UUID uuid) throws Exception {
        Page<Revision<Integer, Conta>> revisions = contaRepository.findRevisions(uuid, PageRequest.of(0, 2));
        List<Revision<Integer, Conta>> content = revisions.getContent();

        var whitelist = List.of("1291", "0950", "8573", "5887", "7617");

        if (content.size() >= 2) {
            var rev = content.get(1);
            contaRepository.save(rev.getEntity());
            sincronizarConta(rev.getEntity());
        } else {
            var conta = contaRepository.findById(uuid);
            if (conta.isPresent() && !whitelist.contains(conta.get().getConta())) {
                contaRepository.deleteById(uuid);
                sincronizarDeleteConta(uuid);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Conta> findMelhoresContas() {
        return contaRepository.findTop3ByOrderBySaldoDesc();
    }

    @Transactional(readOnly = true)
    public Conta findByCpf(String cpf) throws NotFoundException {
        return contaRepository.findByCpf(cpf).orElseThrow(() -> new NotFoundException("Conta não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Conta> findByGerente(String gerente) {
        return contaRepository.findByCpfGerente(gerente);
    }

    @Transactional(readOnly = true)
    public BigDecimal findSumSaldoNegativo(String gerente) {
        return contaRepository.sumSaldosNegativosByCpfGerente(gerente);
    }

    @Transactional(readOnly = true)
    public BigDecimal findSumSaldoPositivo(String gerente) {
        return contaRepository.sumSaldosPositivosByCpfGerente(gerente);
    }

    @Transactional(readOnly = true)
    public List<Conta> findByCpf(List<String> cpf) {
        return contaRepository.findByCpfIn(cpf);
    }

    @Transactional(readOnly = true)
    public Conta getConta(String numConta) {
        return contaRepository.findByConta(numConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }

    public List<Conta> findAll(String filtro, String cpfGerente) {
        if(filtro.equalsIgnoreCase("melhores_clientes")) {
            return findMelhoresContas();
        }
        return contaRepository.findByCpfGerente(cpfGerente);
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
        if (contaRepository.existsByCpf(dto.cpf()))
            throw new IllegalStateException("CPF já cadastrado");

        var numConta = ThreadLocalRandom.current().nextInt(1000, 9999) + "";
        var limite = dto.salario().divide(new BigDecimal(2), RoundingMode.UNNECESSARY);

        Conta conta = new Conta(
                LocalDateTime.now(),
                limite,
                BigDecimal.ZERO,
                numConta,
                dto.cpf(),
                dto.cpfGerente());
        contaRepository.save(conta);

        return conta;
    }

    @Transactional(readOnly = true)
    public Map<String, ContaDTO> findContasByGerentes(String cpfs) {
        var cpfsList = List.of(cpfs.split(","));

        return contaRepository.findByCpfGerenteIn(cpfsList)
            .stream().collect(Collectors.toMap(c -> c.getCpf(), ContaDTO::from));
    }

    @Transactional(readOnly = true)
    public Map<String, SaldoGerenteDTO> findSaldosByGerentes(String cpfs) {
        var cpfsList = cpfs.split(",");
        var maps = new HashMap<String, SaldoGerenteDTO>();
        for (var cpf : cpfsList) {
            var saldoPositivo = contaRepository.sumSaldosPositivosByCpfGerente(cpf);
            var saldoNegativo = contaRepository.sumSaldosNegativosByCpfGerente(cpf);
            maps.put(cpf, new SaldoGerenteDTO(saldoPositivo, saldoNegativo));
        }
        return maps;
    }

    public void sync(Conta conta) {
        DataSourceContextHolder.setContext(DataSourceType.READER);
        contaRepository.save(conta);
    }

    protected void sincronizarMovimentacao(Movimentacao movimentacao) {
        rabbitTemplate.convertAndSend("ms-conta.cqrs.movimentacao", movimentacao);
    }

    protected void sincronizarConta(Conta conta) {
        rabbitTemplate.convertAndSend("ms-conta.cqrs.conta", conta);
    }

    protected void sincronizarDeleteConta(UUID conta) {
        rabbitTemplate.convertAndSend("ms-conta.cqrs.conta.delete", conta);
    }

}
