package com.bantads.cliente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    @GetMapping
    public ResponseEntity<List<Cliente>> findAll(){
        return new ResponseEntity<>(clienteService.findAll(), HttpStatus.OK);
    }

    @GetMapping("product/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable UUID id){
        return new ResponseEntity<>(clienteService.findById(id), HttpStatus.OK);
    }

    @PostMapping("product")
    public ResponseEntity<Cliente> save(@RequestBody Cliente cliente){
        return new ResponseEntity<>(clienteService.save(cliente), HttpStatus.CREATED);
    }
    
    @PutMapping("product")
    public ResponseEntity<Cliente> update(@RequestBody Cliente cliente){
        return new ResponseEntity<>(clienteService.update(cliente), HttpStatus.OK);
    }
    
    @DeleteMapping("product/{id}")
    public void delete(@PathVariable UUID id){
        clienteService.delete(id);
    }
}        
        