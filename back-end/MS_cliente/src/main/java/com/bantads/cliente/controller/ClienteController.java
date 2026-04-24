package com.bantads.cliente.controller;

import com.bantads.cliente.dto.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.AprovarClienteDTO;
import com.bantads.cliente.dto.AprovarClienteResponseDTO;
import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.exceptions.AccountAlredyExists;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    @GetMapping
    public ResponseEntity<List<Cliente>> findAll(){
        return new ResponseEntity<>(clienteService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Cliente> findByCpf(@PathVariable String cpf){
        return new ResponseEntity<>(clienteService.findByCpf(cpf), HttpStatus.OK);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Cliente>> save(@RequestBody ClienteRequestDTO dto) throws IllegalArgumentException {
        return clienteService.startCriarCliente(dto)
                .thenApply(ResponseEntity::ok)
                .orTimeout(15, TimeUnit.SECONDS) // Timeout de segurança
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());
    }

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<AprovarClienteResponseDTO> aprovar(@PathVariable String cpf) throws Exception {
        var dto = clienteService.aprovarCliente(cpf);
        return ResponseEntity.ok(dto);
    }


    @PutMapping(value = "/{cpf}")
    public ResponseEntity<Cliente> update(@PathVariable String cpf, @RequestBody AlterarDadosClienteDTO cliente){
        return new ResponseEntity<>(clienteService.update(cliente, cpf), HttpStatus.OK);
    }

}        
        