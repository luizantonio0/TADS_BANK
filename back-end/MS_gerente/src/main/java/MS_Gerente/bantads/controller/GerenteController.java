package MS_Gerente.bantads.controller;

import MS_Gerente.bantads.dto.request.CriaGerenteDTO;
import MS_Gerente.bantads.model.Gerente;
import MS_Gerente.bantads.dto.request.AtualizaGerenteDTO;
import MS_Gerente.bantads.dto.response.GerenteAtualizadoDTO;
import MS_Gerente.bantads.service.GerenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gerente")
public class GerenteController {

    final GerenteService gerenteService;

    public GerenteController(GerenteService gerenteService) {
        this.gerenteService = gerenteService;
    }


    @GetMapping("product/{cpf}")
    public ResponseEntity<Gerente> findById(@PathVariable String cpf){
        return new ResponseEntity<>(gerenteService.findByCpf(cpf), HttpStatus.OK);
    }

    @PostMapping("product")
    public ResponseEntity<Gerente> save(@RequestBody CriaGerenteDTO criaGerenteDTO){
        return new ResponseEntity<>(gerenteService.save(criaGerenteDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("product/{cpf}")
    public ResponseEntity<GerenteAtualizadoDTO> update(@PathVariable String cpf , @RequestBody AtualizaGerenteDTO atualizaGerenteDTO){
        return new ResponseEntity<>(gerenteService.updateByCpf(cpf, atualizaGerenteDTO), HttpStatus.OK);
    }
    
    @DeleteMapping("product/{cpf}")
    public void delete(@PathVariable String cpf){
        gerenteService.deleteByCpf(cpf);
    }
}        
        