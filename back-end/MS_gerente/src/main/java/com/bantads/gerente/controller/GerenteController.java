package com.bantads.gerente.controller;

import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteAtualizadoDTO;
import com.bantads.gerente.dto.response.GerenteCriadoDTO;
import com.bantads.gerente.exception.BadRequestException;
import com.bantads.gerente.exception.NotFoundException;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.service.OrchestrationService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gerente")
public class GerenteController {

    private final GerenteService gerenteService;
    private final OrchestrationService orchestrationService;

    public GerenteController(GerenteService gerenteService, OrchestrationService orchestrationService) {
        this.gerenteService = gerenteService;
        this.orchestrationService = orchestrationService;
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<GerenteDTO> findById(@PathVariable String cpf) throws Exception {
            return new ResponseEntity<>(GerenteDTO.from(gerenteService.findByCpf(cpf)), HttpStatus.OK);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<GerenteCriadoDTO>> save(@RequestBody CriaGerenteDTO criaGerenteDTO) throws Exception{
            return orchestrationService.startCriarGerente(criaGerenteDTO)
                .thenApply(ResponseEntity::ok)
                .orTimeout(30, TimeUnit.SECONDS);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<GerenteAtualizadoDTO> update(@PathVariable String cpf , @RequestBody AtualizaGerenteDTO atualizaGerenteDTO) throws NotFoundException, BadRequestException{
        return new ResponseEntity<>(gerenteService.updateByCpf(cpf, atualizaGerenteDTO), HttpStatus.OK);
    }
    
    @DeleteMapping("/{cpf}")
    public void delete(@PathVariable String cpf){
        gerenteService.deleteByCpf(cpf);
    }
}        
        