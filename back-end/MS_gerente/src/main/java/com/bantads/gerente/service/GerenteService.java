package com.bantads.gerente.service;

import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.enums.GerenteTipo;
import com.bantads.gerente.exception.BadRequestException;
import com.bantads.gerente.exception.NotFoundException;
import com.bantads.gerente.model.Gerente;
import com.bantads.gerente.repository.GerenteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GerenteService {
    private final GerenteRepository gerenteRepository;
    private final ValidatorService validatorService;

    public GerenteService(GerenteRepository gerenteRepository, ValidatorService validatorService) {
        this.gerenteRepository = gerenteRepository;
        this.validatorService = validatorService;
    }

    @Transactional
    public Gerente novoGerente(CriaGerenteDTO criaGerenteDTO) throws BadRequestException {

        String cpf = validatorService.cpfValidator(criaGerenteDTO.cpf()).orElseThrow(
                () -> new BadRequestException("Cpf informado não é válido"));

        String email = validatorService.emailValidator(criaGerenteDTO.email())
                .orElseThrow(() -> new BadRequestException("Email informado não é válido"));

        if (gerenteRepository.existsByCpf(cpf)) {
            throw new BadRequestException("Já existe um gerente com esse CPF");
        }

        @Valid
        Gerente gerente = new Gerente(criaGerenteDTO);
        gerente.setCpf(cpf);
        gerente.setEmail(email);

        return gerenteRepository.save(gerente);
    }

    public Optional<Gerente> findById(UUID id) {
        return gerenteRepository.findById(id);
    }

    public void deleteByCpf(String cpf) {
        this.gerenteRepository.deleteByCpf(cpf);
    }

    public List<Gerente> findGerentes() {
        return this.gerenteRepository.findByTipoOrderByNomeAsc("GERENTE");
    }

    @Transactional
    public void incrementarCliente(UUID idGerente, String cpfCliente) {

        Optional<Gerente> gerente = gerenteRepository.findById(idGerente);

        if (gerente.isEmpty())
            throw new IllegalArgumentException("Gerente não encontrado");

        Gerente gerenteAtualizado = gerente.get();

        gerenteAtualizado.incrementTotalClientes();
        gerenteAtualizado.getClientes().add(cpfCliente);

        gerenteRepository.save(gerenteAtualizado);
    }

    @Transactional
    public void decrementarCliente(UUID idGerente, String cpfCliente) {

        Optional<Gerente> gerente = gerenteRepository.findById(idGerente);

        if (gerente.isEmpty())
            throw new IllegalArgumentException("Gerente não encontrado");

        Gerente gerenteAtualizado = gerente.get();

        gerenteAtualizado.decrementTotalClientes();
        gerente.get().getClientes().remove(cpfCliente);

        gerenteRepository.save(gerenteAtualizado);
    }

    @Transactional
    public void rollbackGerente(UUID idGerente) {

        Page<Revision<Integer, Gerente>> revisions = gerenteRepository.findRevisions(
                idGerente,
                PageRequest.of(
                        0,
                        2,
                        Sort.by(
                                Sort.Direction.DESC,
                                "metadata.revisionNumber")));

        List<Revision<Integer, Gerente>> content = revisions.getContent();

        var whitelist = List.of(
                "98574307084",
                "64065268052",
                "23862179060",
                "40501740066");

        if (content.size() >= 2) {

            Gerente revisaoAnterior = content.get(1).getEntity();

            Gerente gerenteAtual = gerenteRepository.findById(idGerente)
                    .orElseThrow(() -> new RuntimeException(
                            "Gerente não encontrado"));

            gerenteAtual.setCpf(revisaoAnterior.getCpf());
            gerenteAtual.setNome(revisaoAnterior.getNome());
            gerenteAtual.setEmail(revisaoAnterior.getEmail());
            gerenteAtual.setTipo(GerenteTipo.valueOf(revisaoAnterior.getTipo()));
            gerenteAtual.setTotalClientes(
                    revisaoAnterior.getTotalClientes());

            gerenteRepository.save(gerenteAtual);

            return;
        }

        gerenteRepository.findById(idGerente)
                .ifPresent(gerente -> {
                    if (!whitelist.contains(
                            gerente.getCpf())) {
                        gerenteRepository.delete(gerente);
                    }
                });
    }

    @Transactional
    public Gerente updateByCpf(String cpf, AtualizaGerenteDTO atualizaGerenteDTO)
            throws NotFoundException, BadRequestException {

        String email = validatorService.emailValidator(atualizaGerenteDTO.email()).orElseThrow(
                () -> new BadRequestException("Email informado não é válido"));
        @Valid
        Gerente gerente = gerenteRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Gerente não encontrado!"));

        gerente.setNome(atualizaGerenteDTO.nome());
        gerente.setEmail(email);

        gerenteRepository.save(gerente);

        return gerente;
    }

    public Gerente findByCpf(String cpf) throws NotFoundException {
        return gerenteRepository.findByCpf(cpf).orElseThrow(
                () -> new NotFoundException("Gerente não encontrado!"));
    }

    public Optional<Gerente> findGerenteMenosClientes() {
        return Optional.ofNullable(gerenteRepository.findTop1GerenteComMenosClientes().getFirst());
    }

    @Transactional
    public void reboot() {
        gerenteRepository.deleteAll();
        gerenteRepository.flush();
        Gerente g1 = new Gerente();
        g1.setCpf("98574307084");
        g1.setNome("Geniéve");
        g1.setEmail("ger1@bantads.com.br");
        g1.setTipo(com.bantads.gerente.enums.GerenteTipo.GERENTE);
        g1.setTotalClientes(2);

        Gerente g2 = new Gerente();
        g2.setCpf("64065268052");
        g2.setNome("Godophredo");
        g2.setEmail("ger2@bantads.com.br");
        g2.setTipo(com.bantads.gerente.enums.GerenteTipo.GERENTE);
        g2.setTotalClientes(2);

        Gerente g3 = new Gerente();
        g3.setCpf("23862179060");
        g3.setNome("Gyândula");
        g3.setEmail("ger3@bantads.com.br");
        g3.setTipo(com.bantads.gerente.enums.GerenteTipo.GERENTE);
        g3.setTotalClientes(1);

        Gerente g4 = new Gerente();
        g4.setCpf("40501740066");
        g4.setNome("Adamântio");
        g4.setEmail("adm1@bantads.com.br");
        g4.setTipo(com.bantads.gerente.enums.GerenteTipo.ADMINISTRADOR);
        g4.setTotalClientes(0);

        gerenteRepository.saveAll(List.of(g1, g2, g3, g4));
    }
}
