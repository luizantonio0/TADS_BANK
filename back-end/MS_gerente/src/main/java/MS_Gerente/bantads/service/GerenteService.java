package main.java.MS_Gerente.bantads.service;

import main.java.MS_Gerente.bantads.dto.request.AtualizaGerenteDTO;
import main.java.MS_Gerente.bantads.dto.response.GerenteAtualizadoDTO;
import main.java.MS_Gerente.bantads.mapper.GerenteMapper;
import main.java.MS_Gerente.bantads.model.Gerente;
import main.java.MS_Gerente.bantads.repository.GerenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GerenteService {
    private final GerenteRepository gerenteRepository;
    private final GerenteMapper gerenteMapper;

    public GerenteService(GerenteRepository gerenteRepository, GerenteMapper gerenteMapper) {
        this.gerenteRepository = gerenteRepository;
        this.gerenteMapper = gerenteMapper;
    }

    public List<Gerente> findAll() {
        return gerenteRepository.findAll(); 
    }
    
    public Gerente findById(UUID id){
        return gerenteRepository.findById(id).orElse(null);
    }

    public Gerente save(Gerente gerente){
        return gerenteRepository.save(gerente);
    }
    
    public Gerente update(Gerente gerente){
        return gerenteRepository.save(gerente);
    }

    public void deleteById(UUID id){
        gerenteRepository.deleteById(id);
    }

    public void deleteByCpf(String cpf) {

    }

    public GerenteAtualizadoDTO updateByCpf(String cpf, AtualizaGerenteDTO atualizaGerenteDTO) {
        var gerente = gerenteRepository.findByCpf(cpf);

        gerenteMapper.ataualizaGerentePeloDto(atualizaGerenteDTO, gerente);

        gerenteRepository.save(gerente);

        return new GerenteAtualizadoDTO(gerente);
    }

    public Gerente findByCpf(String cpf) {
        return gerenteRepository.findByCpf(cpf);
    }
}
        