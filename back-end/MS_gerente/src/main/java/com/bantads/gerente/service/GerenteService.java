package com.bantads.gerente.service;

import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.exception.BadRequestException;
import com.bantads.gerente.exception.NotFoundException;
import com.bantads.gerente.model.Gerente;
import com.bantads.gerente.repository.GerenteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import com.bantads.gerente.dto.response.GerenteAtualizadoDTO;
import com.bantads.gerente.mapper.GerenteMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GerenteService {
    private final GerenteRepository gerenteRepository;
    private final GerenteMapper gerenteMapper;
    private final ValidatorService validatorService;

    public GerenteService(GerenteRepository gerenteRepository, GerenteMapper gerenteMapper, ValidatorService validatorService) {
        this.gerenteRepository = gerenteRepository;
        this.gerenteMapper = gerenteMapper;
        this.validatorService = validatorService;
    }

    @Transactional
    public Gerente novoGerente(CriaGerenteDTO criaGerenteDTO) throws BadRequestException {

        String cpf = validatorService.cpfValidator(criaGerenteDTO.cpf()).
                orElseThrow(
                        () -> new BadRequestException("Cpf informado não é válido")
                );

        String email = validatorService.emailValidator(criaGerenteDTO.email()).orElseThrow(() -> new BadRequestException("Email informado não é válido"));

        if(gerenteRepository.existsByCpf(cpf)) {
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
        return this.gerenteRepository.findByTipo("GERENTE");
    }


    @Transactional
    public void incrementarCliente(UUID idGerente) {

        Optional<Gerente> gerente = gerenteRepository.findById(idGerente);

        if (gerente.isEmpty()) throw new IllegalArgumentException("Gerente não encontrado");

        Gerente gerenteAtualizado = gerente.get();

        gerenteAtualizado.incrementTotalClientes();

        gerenteRepository.save(gerenteAtualizado);
    }

    @Transactional
    public void decrementarCliente(UUID idGerente) {

        Optional<Gerente> gerente = gerenteRepository.findById(idGerente);

        if (gerente.isEmpty()) throw new IllegalArgumentException("Gerente não encontrado");

        Gerente gerenteAtualizado = gerente.get();

        gerenteAtualizado.decrementTotalClientes();

        gerenteRepository.save(gerenteAtualizado);
    }

    @Transactional
    public void rollbackGerente(UUID idGerente) {
        Page<Revision<Integer, Gerente>> revisions = gerenteRepository.findRevisions(
                idGerente,
                PageRequest.of(0, 2)
        );

        List<Revision<Integer, Gerente>> content = revisions.getContent();
        var whitelist = List.of("98574307084", "64065268052", "23862179060", "40501740066");

        if (content.size() >= 2) {
            var rev = content.get(1);
            gerenteRepository.save(rev.getEntity());
            return;
        } else {
            var cpf = gerenteRepository.findById(idGerente);
            if(cpf.isPresent() && !whitelist.contains(cpf.get().getCpf()))
                gerenteRepository.deleteById(idGerente);
        }

    }

    @Transactional
    public Gerente updateByCpf(String cpf, AtualizaGerenteDTO atualizaGerenteDTO) throws NotFoundException, BadRequestException {

        String email = validatorService.emailValidator(atualizaGerenteDTO.email()).
                orElseThrow(
                        () -> new BadRequestException("Email informado não é válido")
                );
        @Valid
        Gerente gerente = gerenteRepository.findByCpf(cpf).orElseThrow(() -> new NotFoundException("Gerente não encontrado!"));

        gerenteMapper.ataualizaGerentePeloDto(atualizaGerenteDTO, gerente);

        gerente.setEmail(email);

        gerenteRepository.save(gerente);

        return gerente;
    }

    public Gerente findByCpf(String cpf) throws NotFoundException {
        return gerenteRepository.findByCpf(cpf).orElseThrow(
                () -> new NotFoundException("Gerente não encontrado!")
        );
    }

    public Optional<Gerente> findGerenteMenosClientes() {
        return gerenteRepository.findTop1GerenteComMenosClientes();
    }
}
        