package com.bantads.gerente.controller;

import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.exception.AccountAlredyExists;
import com.bantads.gerente.model.Gerente;
import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteAtualizadoDTO;
import com.bantads.gerente.service.GerenteService;
import org.springframework.data.crossstore.ChangeSetPersister;
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


    @GetMapping("gerente/{cpf}")
    public ResponseEntity<Gerente> findById(@PathVariable String cpf){
            return new ResponseEntity<>(gerenteService.findByCpf(cpf), HttpStatus.OK);
    }

    @PostMapping("gerente")
    public ResponseEntity<Gerente> save(@RequestBody CriaGerenteDTO criaGerenteDTO){
            return new ResponseEntity<>(
                    gerenteService.save(criaGerenteDTO),
                    HttpStatus.CREATED
            );
    }

    @PutMapping("gerente/{cpf}")
    public ResponseEntity<GerenteAtualizadoDTO> update(@PathVariable String cpf , @RequestBody AtualizaGerenteDTO atualizaGerenteDTO){
        return new ResponseEntity<>(gerenteService.updateByCpf(cpf, atualizaGerenteDTO), HttpStatus.OK);
    }
    
    @DeleteMapping("gerente/{cpf}")
    public void delete(@PathVariable String cpf){
        gerenteService.deleteByCpf(cpf);
    }
}        
        