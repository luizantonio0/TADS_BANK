package main.java.MS_Gerente.bantads.service;

import main.java.MS_Gerente.bantads.model.Gerente;
import main.java.MS_Gerente.bantads.repository.GerenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GerenteService {
    private final GerenteRepository gerenteRepository;

    public GerenteService(GerenteRepository gerenteRepository) {
        this.gerenteRepository = gerenteRepository;
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
}
        