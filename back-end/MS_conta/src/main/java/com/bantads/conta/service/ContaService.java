package com.bantads.conta.service;

import com.bantads.conta.datasource.DataSourceContextHolder;
import com.bantads.conta.datasource.DataSourceType;
import com.bantads.conta.dto.ContaCreateInputDTO;
import com.bantads.conta.dto.ContaDTO;
import com.bantads.conta.dto.SaldoGerenteDTO;
import com.bantads.conta.dto.cqrs.CQRSSyncEntity;
import com.bantads.conta.exception.ForbiddenException;
import com.bantads.conta.exception.HttpException;
import com.bantads.conta.exception.NotFoundException;
import com.bantads.conta.model.Conta;
import com.bantads.conta.model.Movimentacao;
import com.bantads.conta.repository.read.ContaReadRepository;
import com.bantads.conta.repository.write.ContaRepository;

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
import java.util.Arrays;
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
    private ContaReadRepository readRepository;
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

    public Conta updateGerente(String cpfCliente, String cpfGerente) throws HttpException {
        var conta = contaRepository.findByCpf(cpfCliente).orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        conta.setCpfGerente(cpfGerente);
        contaRepository.save(conta);
        return conta;
    }

    public Conta findConta(String conta, String cpfLogado, String profile) throws HttpException {
        var contaOpt = contaRepository.findByConta(conta);
        if (contaOpt.isEmpty()) {
            throw new NotFoundException("Conta não encontrada");
        }
        var contaEntity = contaOpt.get();
        if (!contaEntity.getCpf().equals(cpfLogado) && !profile.equalsIgnoreCase("ADMINISTRADOR") && !contaEntity.getCpfGerente().equals(cpfLogado)) {
            throw new ForbiddenException("Você não tem permissão para acessar essa conta");
        }
        return contaEntity;
    }

    @Transactional(readOnly = true)
    public List<Conta> findMelhoresContas() {
        return contaRepository.findTop3ByOrderBySaldoDesc();
    }

    @Transactional
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
    public List<Conta> findByContaNum(List<String> contas) {
        return contaRepository.findByCpfIn(contas);
    }


    @Transactional(readOnly = true)
    public Conta getConta(String numConta) {
        return contaRepository.findByConta(numConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Conta> findAll(String filtro, String cpfGerente, String profile, String contas) {
        if(filtro.equalsIgnoreCase("melhores_clientes")) {
            return findMelhoresContas();
        }
        if(filtro.equalsIgnoreCase("contas")) {
            return contaRepository.findByContaIn(Arrays.asList(contas.split(",")));
        }
        if(cpfGerente != null && !profile.equalsIgnoreCase("ADMINISTRADOR") && !cpfGerente.isBlank()) {
            return contaRepository.findByCpfGerente(cpfGerente);
        }
        return contaRepository.findAll();
    }

    public Conta atualizarLimite(String cpf, BigDecimal salario) {
        var optConta = contaRepository.findByCpf(cpf);
        if (optConta.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada");
        }

        var conta = optConta.get();
        var newLimite = salario.divide(new BigDecimal(2), RoundingMode.UNNECESSARY);
        if (conta.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            // RF04: Se o novo limite for menor que o seu saldo negativo neste momento, 
            // então seu limite será ajustado para seu saldo negativo
            newLimite = conta.getSaldo().abs();
        }
        conta.setLimite(newLimite);
        contaRepository.save(conta);

        sincronizarConta(conta);
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

        sincronizarConta(conta);
        return conta;
    }

    @Transactional(readOnly = true)
    public Map<String, ContaDTO> findContasByGerentes(String cpfs) {
        var cpfsList = List.of(cpfs.split(","));

        return contaRepository.findByCpfGerenteIn(cpfsList)
            .stream().collect(Collectors.toMap(c -> c.getCpf(), ContaDTO::from));
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> findSaldosPositivos() {
        var cpfsList = contaRepository.findDistinctGerentes();

        var map = new HashMap<String, BigDecimal>();
        for(var g : cpfsList) {
            var menorSaldo = contaRepository.sumSaldosPositivosByCpfGerente(g);
            map.put(g, menorSaldo);
        }

        return map;
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

    public void sync(CQRSSyncEntity.ContaDTO m) {
        DataSourceContextHolder.setContext(DataSourceType.READER);
        Conta c = new Conta();
        c.setConta(m.conta());
        c.setCpf(m.cpf());
        c.setCpfGerente(m.cpfGerente());
        c.setCriacao(LocalDateTime.parse(m.criacao()));
        c.setLimite(m.limite());
        c.setSaldo(m.saldo());
        c.setId(m.id());
        readRepository.save(c);
    }

    protected void sincronizarMovimentacao(Movimentacao movimentacao) {
        var dto = CQRSSyncEntity.MovimentacaoDTO.from(movimentacao);
        rabbitTemplate.convertAndSend("ms-conta.cqrs.movimentacao", dto);
    }

    protected void sincronizarConta(Conta conta) {
        var dto = CQRSSyncEntity.ContaDTO.from(conta);
        rabbitTemplate.convertAndSend("ms-conta.cqrs.conta", dto);
    }

    protected void sincronizarDeleteConta(UUID conta) {
        rabbitTemplate.convertAndSend("ms-conta.cqrs.conta.delete", conta);
    }

    @Transactional
    public void reboot(DataSourceType context) {
        DataSourceContextHolder.setContext(context);
        var repository = context == DataSourceType.WRITER ? contaRepository : readRepository;
        repository.deleteAll();
        repository.flush();
        Conta conta1 = new Conta(
                LocalDateTime.now(),
                new BigDecimal("5000.00"),
                new BigDecimal("800.00"),
                "1291",
                "12912861012",
                "98574307084");
        conta1.setId(UUID.fromString("c5ed645a-05ef-4a21-a847-4ecc0622cb58"));

        Conta conta2 = new Conta(
                LocalDateTime.now(),
                new BigDecimal("10000.00"),
                new BigDecimal("-10000.00"),
                "0950",
                "09506382000",
                "64065268052");
        conta2.setId(UUID.fromString("1b732a9b-82e9-4baf-86d4-737c4d3a34af"));

        Conta conta3 = new Conta(
                LocalDateTime.now(),
                new BigDecimal("1500.00"),
                new BigDecimal("-1000.00"),
                "8573",
                "85733854057",
                "23862179060");
        conta3.setId(UUID.fromString("cb30db11-e65c-44e5-97da-b1fc3d95ed40"));

        Conta conta4 = new Conta(
                LocalDateTime.now(),
                new BigDecimal("0.00"),
                new BigDecimal("150000.00"),
                "5887",
                "58872160006",
                "98574307084");
        conta4.setId(UUID.fromString("e44251f8-dfa1-4638-8c6e-3c2edf815cf1"));

        Conta conta5 = new Conta(
                LocalDateTime.now(),
                new BigDecimal("0.00"),
                new BigDecimal("1500.00"),
                "7617",
                "76179646090",
                "64065268052");
        conta5.setId(UUID.fromString("d67612cc-ea11-4667-82b2-b1b48cd6e017"));
        repository.saveAll(List.of(conta1, conta2, conta3, conta4, conta5));
    }

}
