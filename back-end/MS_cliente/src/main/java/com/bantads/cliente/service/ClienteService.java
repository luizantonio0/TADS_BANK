package com.bantads.cliente.service;

import com.bantads.cliente.dto.ClienteDTO;
import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.enums.LogStatus;
import com.bantads.cliente.enums.UF;
import com.bantads.cliente.exception.BadRequestException;
import com.bantads.cliente.exception.ForbiddenException;
import com.bantads.cliente.exception.HttpException;
import com.bantads.cliente.exception.NotFoundException;
import com.bantads.cliente.mapper.ClienteMapper;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.model.LogStatusCliente;
import com.bantads.cliente.repository.ClienteRepository;
import com.bantads.cliente.repository.LogStatusRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private LogStatusRepository logStatusRepository;
    @Autowired
    private ClienteMapper mapper;

    public List<Cliente> findClientes(String cpfLogado, String profileLogado, String filtro, String nome, String orderBy)
            throws HttpException {

        var isGerente = profileLogado.equalsIgnoreCase("GERENTE");
        var isAdmin = profileLogado.equalsIgnoreCase("ADMINISTRADOR");

        if (!isGerente && !isAdmin) {
            throw new ForbiddenException("Você não tem permissão para efetuar esta operação");
        }

        if (filtro.equals("para_aprovar")) {
            if (!isGerente)
                throw new ForbiddenException("Você não tem permissão para efetuar esta operação");
            return clienteRepository.findByCpfGerenteAndAprovadoOrderByCriacaoAsc(cpfLogado, false);
        }

        if (isAdmin) {
            var sort = switch(orderBy) {
                case "criacao" -> Sort.by("criacao").descending();
                case "nome" -> Sort.by("nome").ascending();
                default -> Sort.by("criacao").descending();
            };
            return clienteRepository.findAll(sort);
        }

        return null;
    }

    public Map<String, String> findNomesByCpf(List<String> cpfs) {
        return clienteRepository.findByCpfIn(cpfs).stream()
                .collect(Collectors.toMap(c -> c.getCpf(), c -> c.getNome()));
    }

    public Cliente findByCpf(String cpf) throws NotFoundException {
        return clienteRepository.findByCpf(cpf).orElseThrow(() -> new NotFoundException("Cliente não encontrado!"));
    }

    public List<Cliente> findByGerente(String cpf) {
        return clienteRepository.findByGerente(cpf, "");
    }

    public Map<String, List<ClienteDTO>> findClientesByGerentes(String cpfs) {
        var cpfsList = cpfs.split(",");
        var maps = new HashMap<String, List<ClienteDTO>>();
        for (var cpf : cpfsList)
            maps.put(cpf, clienteRepository.findByGerente(cpf, "").stream().map(ClienteDTO::from).toList());
        return maps;
    }

    public List<Cliente> findClientesByGerente(String cpf) {
        return clienteRepository.findByGerente(cpf, "");
    }

    public List<Cliente> findByCpf(List<String> cpf) {
        return clienteRepository.findByCpfIn(cpf);
    }

    public Cliente cadastrarCliente(ClienteRequestDTO dto) throws BadRequestException {
        var cpf = dto.cpf().replaceAll("[^0-9]", "");
        if (clienteRepository.existsByCpf(cpf)) {
            throw new BadRequestException("Este CPF já está em uso!");
        }
        Cliente cliente = new Cliente(dto);
        return clienteRepository.save(cliente);
    }

    public Cliente update(AlterarDadosClienteDTO dto, String cpf) {
        var clienteAtual = clienteRepository.findByCpf(cpf);

        if (clienteAtual.isEmpty())
            return null;

        var cliente = clienteAtual.get();

        mapper.updateEntityFromDto(dto, cliente);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente aprovarCliente(String cpf) throws Exception {
        var cliente = clienteRepository.findByCpf(cpf);
        if (cliente.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado");
        }
        cliente.get().setAprovado(true);
        logStatusRepository
                .save(new LogStatusCliente(cliente.get().getId(), cpf, LogStatus.APROVADO, "", LocalDateTime.now()));
        clienteRepository.save(cliente.get());
        return cliente.get();
    }

    @Transactional
    public Cliente rejeitarCliente(String cpf, String motivo) throws Exception {
        var cliente = clienteRepository.findByCpf(cpf);
        if (cliente.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado");
        }
        logStatusRepository.save(
                new LogStatusCliente(cliente.get().getId(), cpf, LogStatus.REJEITADO, motivo, LocalDateTime.now()));
        clienteRepository.delete(cliente.get());
        return cliente.get();
    }

    public void rollbackLogStatus(UUID uuid) throws Exception {
        Page<Revision<Integer, LogStatusCliente>> revisions = logStatusRepository.findRevisions(uuid,
                PageRequest.of(0, 2));
        List<Revision<Integer, LogStatusCliente>> content = revisions.getContent();

        if (content.size() >= 2) {
            var rev = content.get(1);
            logStatusRepository.save(rev.getEntity());
        } else {
            var cpf = clienteRepository.findById(uuid);
            if (cpf.isPresent())
                logStatusRepository.deleteById(uuid);
        }
    }

    @Transactional
    public void rollbackCliente(UUID uuid) throws Exception {

        Page<Revision<Integer, Cliente>> revisions = clienteRepository.findRevisions(uuid, PageRequest.of(0, 2));

        List<Revision<Integer, Cliente>> content = revisions.getContent();

        var whitelist = List.of(
                "12912861012",
                "09506382000",
                "85733854057",
                "58872160006",
                "76179646090");

        if (content.size() >= 2) {

            Cliente revisionEntity = content.get(1).getEntity();

            Cliente atual = clienteRepository.findById(uuid)
                    .orElseThrow();

            atual.setCpf(revisionEntity.getCpf());
            atual.setNome(revisionEntity.getNome());
            atual.setEmail(revisionEntity.getEmail());
            atual.setSalario(revisionEntity.getSalario());
            atual.setTelefone(revisionEntity.getTelefone());
            atual.setAprovado(revisionEntity.isAprovado());
            atual.setCpfGerente(revisionEntity.getCpfGerente());
            atual.setEstado(revisionEntity.getEstado());
            atual.setCidade(revisionEntity.getCidade());
            atual.setCep(revisionEntity.getCep());
            atual.setEndereco(revisionEntity.getEndereco());

            clienteRepository.save(atual);

        } else {

            var cpf = clienteRepository.findById(uuid);

            if (cpf.isPresent() &&
                    !whitelist.contains(cpf.get().getCpf())) {

                clienteRepository.deleteById(uuid);
            }
        }
    }

    @Transactional
    public void reboot() {
        clienteRepository.deleteAll();
        logStatusRepository.deleteAll();

        clienteRepository.flush();

        Cliente c1 = new Cliente();
        c1.setCpf("12912861012");
        c1.setNome("Catharyna");
        c1.setEmail("cli1@bantads.com.br");
        c1.setSalario(new BigDecimal("10000.00"));
        c1.setTelefone("19948208842");
        c1.setAprovado(true);
        c1.setCpfGerente("98574307084");
        c1.setEstado(UF.SE);
        c1.setCidade("Aracaju");
        c1.setCep("49048320");
        c1.setEndereco("Rua Radialista Wolney Silva, 100, Luzia");
        c1.setCriacao(LocalDateTime.now());

        Cliente c2 = new Cliente();
        c2.setCpf("09506382000");
        c2.setNome("Cleuddônio");
        c2.setEmail("cli2@bantads.com.br");
        c2.setSalario(new BigDecimal("20000.00"));
        c2.setTelefone("41995292929");
        c2.setAprovado(true);
        c2.setCpfGerente("64065268052");
        c2.setEstado(UF.SC);
        c2.setCidade("Brusque");
        c2.setCep("88354670");
        c2.setEndereco("Rua Maximiliano Furbringer, 500, Jardim Maluche");
        c2.setCriacao(LocalDateTime.now());

        Cliente c3 = new Cliente();
        c3.setCpf("85733854057");
        c3.setNome("Catianna");
        c3.setEmail("cli3@bantads.com.br");
        c3.setSalario(new BigDecimal("3000.00"));
        c3.setTelefone("22924402941");
        c3.setAprovado(true);
        c3.setCpfGerente("23862179060");
        c3.setEstado(UF.SE);
        c3.setCidade("Aracaju");
        c3.setCep("49030790");
        c3.setEndereco("Rua Edson de Oliveira, 33, Farolândia");
        c3.setCriacao(LocalDateTime.now());

        Cliente c4 = new Cliente();
        c4.setCpf("58872160006");
        c4.setNome("Cutardo");
        c4.setEmail("cli4@bantads.com.br");
        c4.setSalario(new BigDecimal("500.00"));
        c4.setTelefone("87992429912");
        c4.setAprovado(true);
        c4.setCpfGerente("98574307084");
        c4.setEstado(UF.AC);
        c4.setCidade("Rio Branco");
        c4.setCep("69902136");
        c4.setEndereco("Rua Juruá, 200, Loteamento Jardim São Francisco");
        c4.setCriacao(LocalDateTime.now());

        Cliente c5 = new Cliente();
        c5.setCpf("76179646090");
        c5.setNome("Coândrya");
        c5.setEmail("cli5@bantads.com.br");
        c5.setSalario(new BigDecimal("1500.00"));
        c5.setTelefone("18989882942");
        c5.setAprovado(true);
        c5.setCpfGerente("64065268052");
        c5.setEstado(UF.BA);
        c5.setCidade("Salvador");
        c5.setCep("40393700");
        c5.setEndereco("Travessa Candiubá, 39, Capelinha");
        c5.setCriacao(LocalDateTime.now());
        clienteRepository.saveAll(List.of(c1, c2, c3, c4, c5));
    }

}
