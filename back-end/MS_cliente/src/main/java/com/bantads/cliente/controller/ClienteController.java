package com.bantads.cliente.controller;

import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.dto.ClienteRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("{id}")
    public ResponseEntity<Cliente> findById(@PathVariable UUID id){
        return new ResponseEntity<>(clienteService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cliente> save(@RequestBody ClienteRequestDTO cliente){
        return new ResponseEntity<>(clienteService.save(cliente), HttpStatus.CREATED);
    }
    
    @PutMapping
    public ResponseEntity<Cliente> update(@RequestBody Cliente cliente){
        return new ResponseEntity<>(clienteService.update(cliente), HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id){
        clienteService.delete(id);
    }
}        
        