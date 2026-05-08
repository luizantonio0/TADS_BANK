package com.bantads.cliente.controller;

import com.bantads.cliente.dto.*;
import com.bantads.cliente.exceptions.AccountAlredyExists;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.service.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired private ClienteService clienteService;
    @Autowired private OrchestrationService orchestrationService;

    @GetMapping
    public ResponseEntity<List<Cliente>> findAll(){
        return new ResponseEntity<>(clienteService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Cliente> findByCpf(@PathVariable String cpf){
        return new ResponseEntity<>(clienteService.findByCpf(cpf), HttpStatus.OK);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ClienteCreateResponseDTO>> save(@RequestBody ClienteRequestDTO dto) throws Exception {
        return orchestrationService.startCriarCliente(dto)
                .thenApply(ResponseEntity::ok)
                .orTimeout(15, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    ex.printStackTrace(); // Veja no console qual é a exceção real
                    return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
                });
    }

    @PostMapping("/{cpf}/aprovar")
    public CompletableFuture<ResponseEntity<AprovarClienteResponseDTO>> aprovar(@PathVariable("cpf") String cpf) throws Exception {
        return orchestrationService.startAprovarCliente(new AprovarClienteDTO(cpf.replaceAll("[^0-9]", "")))
                .thenApply(ResponseEntity::ok)
                .orTimeout(15, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    ex.printStackTrace(); // Veja no console qual é a exceção real
                    return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
                });
    }


    @PutMapping(value = "/{cpf}")
    public ResponseEntity<Cliente> update(@PathVariable String cpf, @RequestBody AlterarDadosClienteDTO cliente){
        return new ResponseEntity<>(clienteService.update(cliente, cpf), HttpStatus.OK);
    }

}        
        