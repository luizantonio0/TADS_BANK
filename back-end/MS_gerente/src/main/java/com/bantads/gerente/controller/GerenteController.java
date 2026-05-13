package com.bantads.gerente.controller;

import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.model.Gerente;
import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteAtualizadoDTO;
import com.bantads.gerente.service.GerenteService;
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
    public ResponseEntity<GerenteDTO> findById(@PathVariable String cpf) throws Exception {
            return new ResponseEntity<>(GerenteDTO.from(gerenteService.findByCpf(cpf).orElseThrow(() -> new Exception("Cliente não encontrado"))), HttpStatus.OK);
    }

    @PostMapping("gerente")
    public ResponseEntity<Gerente> save(@RequestBody CriaGerenteDTO criaGerenteDTO){
            return new ResponseEntity<>(gerenteService.novoGerente(criaGerenteDTO), HttpStatus.CREATED);
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
        