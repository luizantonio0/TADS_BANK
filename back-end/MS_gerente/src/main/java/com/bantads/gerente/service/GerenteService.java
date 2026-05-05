package com.bantads.gerente.service;

import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.exception.AccountAlredyExists;
import com.bantads.gerente.exception.NotFoundExecption;
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

    public GerenteService(GerenteRepository gerenteRepository, GerenteMapper gerenteMapper) {
        this.gerenteRepository = gerenteRepository;
        this.gerenteMapper = gerenteMapper;
    }

    @Transactional
    public Gerente save(CriaGerenteDTO criaGerenteDTO) throws AccountAlredyExists {

        gerenteRepository.findByCpf(criaGerenteDTO.cpf())
                .orElseThrow(
                        () -> new AccountAlredyExists("Já existe um gerente com esse CPF")
                );

        @Valid
        Gerente gerente = new Gerente(criaGerenteDTO);


        return gerenteRepository.save(gerente);
    }

    public Optional<Gerente> findById(UUID id) {
        return gerenteRepository.findById(id);
    }


    @Transactional
    public void deleteByCpf(String cpf) {
        this.gerenteRepository.deleteByCpf(cpf);
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
    public void rollbackGerente(UUID idGerente) {
        Page<Revision<Integer, Gerente>> revisions = gerenteRepository.findRevisions(
                idGerente,
                PageRequest.of(0, 2)
        );

        List<Revision<Integer, Gerente>> content = revisions.getContent();

        if (content.size() >= 2) {
            var revision = content.get(1).getEntity();
            gerenteRepository.save(revision);
        } else {
            gerenteRepository.deleteById(idGerente);
        }
    }

    @Transactional
    public GerenteAtualizadoDTO updateByCpf(String cpf, AtualizaGerenteDTO atualizaGerenteDTO) throws NotFoundExecption {

        @Valid
        Gerente gerente = gerenteRepository.findByCpf(cpf).orElseThrow(
                () -> new NotFoundExecption("")
        );

        gerenteMapper.ataualizaGerentePeloDto(atualizaGerenteDTO, gerente);

        gerenteRepository.save(gerente);

        return new GerenteAtualizadoDTO(gerente);
    }

    public Optional<Gerente> findByCpf(String cpf) throws NotFoundExecption {
        return gerenteRepository.findByCpf(cpf);
    }

    public Optional<Gerente> findGerenteMenosClientes() {
        return gerenteRepository.findFirstByOrderByTotalClientesAsc();
    }
}
        