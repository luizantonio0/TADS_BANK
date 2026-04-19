package MS_Gerente.bantads.service;

import MS_Gerente.bantads.dto.request.AtualizaGerenteDTO;
import MS_Gerente.bantads.dto.request.CriaGerenteDTO;
import MS_Gerente.bantads.model.Gerente;
import MS_Gerente.bantads.repository.GerenteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import MS_Gerente.bantads.dto.response.GerenteAtualizadoDTO;
import MS_Gerente.bantads.mapper.GerenteMapper;
import org.springframework.stereotype.Service;

@Service
public class GerenteService {
    private final GerenteRepository gerenteRepository;
    private final GerenteMapper gerenteMapper;

    public GerenteService(GerenteRepository gerenteRepository, GerenteMapper gerenteMapper) {
        this.gerenteRepository = gerenteRepository;
        this.gerenteMapper = gerenteMapper;
    }

    @Transactional
    public Gerente save(CriaGerenteDTO criaGerenteDTO){

        @Valid
        Gerente gerente = new Gerente(criaGerenteDTO);

        return gerenteRepository.save(gerente);
    }
    
    public Gerente update(Gerente gerente){
        return gerenteRepository.save(gerente);
    }

    public void deleteByCpf(String cpf) {
        this.gerenteRepository.deleteByCpf(cpf);
    }

    public GerenteAtualizadoDTO updateByCpf(String cpf, AtualizaGerenteDTO atualizaGerenteDTO) {

        @Valid
        Gerente gerente = gerenteRepository.findByCpf(cpf);

        gerenteMapper.ataualizaGerentePeloDto(atualizaGerenteDTO, gerente);

        gerenteRepository.save(gerente);

        return new GerenteAtualizadoDTO(gerente);
    }

    public Gerente findByCpf(String cpf) {
        return gerenteRepository.findByCpf(cpf);
    }
}
        