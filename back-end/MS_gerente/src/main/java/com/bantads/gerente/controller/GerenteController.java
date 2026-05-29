package com.bantads.gerente.controller;

import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.dto.response.GerenteAtualizadoDTO;
import com.bantads.gerente.dto.response.GerenteCriadoDTO;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.service.OrchestrationService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gerentes")
public class GerenteController {

    private final GerenteService gerenteService;
    private final OrchestrationService orchestrationService;

    public GerenteController(GerenteService gerenteService, OrchestrationService orchestrationService) {
        this.gerenteService = gerenteService;
        this.orchestrationService = orchestrationService;
    }

    @GetMapping
    public ResponseEntity<List<GerenteDTO>> findGerentes() throws Exception {
        return ResponseEntity.ok(gerenteService.findGerentes().stream().map(c -> GerenteDTO.from(c, false)).toList());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<GerenteDTO> findById(@PathVariable("cpf") String cpf) throws Exception {
            return new ResponseEntity<>(GerenteDTO.from(gerenteService.findByCpf(cpf), false), HttpStatus.OK);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<GerenteCriadoDTO>> save(@RequestBody CriaGerenteDTO criaGerenteDTO) throws Exception{
            return orchestrationService.startCriarGerente(criaGerenteDTO)
                .thenApply((c) -> ResponseEntity.status(201).body(c))
                .orTimeout(30, TimeUnit.SECONDS);
    }

    @PutMapping("/{cpf}")
    public CompletableFuture<ResponseEntity<GerenteAtualizadoDTO>> update(
        @PathVariable("cpf") String cpf , 
        @RequestBody AtualizaGerenteDTO atualizaGerenteDTO
    ) throws Exception {
        cpf = cpf.replaceAll("[^0-9]", "");
        var dto = new AtualizaGerenteDTO(
                cpf,
                atualizaGerenteDTO.nome(),
                atualizaGerenteDTO.email(),
                atualizaGerenteDTO.senha()
        );
        return orchestrationService.startAtualizarGerente(cpf, dto)
                .thenApply(ResponseEntity::ok)
                .orTimeout(30, TimeUnit.SECONDS);

    }
    
    @DeleteMapping("/{cpf}")
    public void delete(@PathVariable String cpf){
        gerenteService.deleteByCpf(cpf);
    }

    @GetMapping("/reboot")
    public ResponseEntity<?> reboot() {
        gerenteService.reboot();
        return ResponseEntity.ok("");
    }

}        
        