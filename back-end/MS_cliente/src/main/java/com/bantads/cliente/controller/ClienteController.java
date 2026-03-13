package com.bantads.cliente.controller;

import com.bantads.cliente.dto.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.AprovarClienteDTO;
import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.exceptions.AccountAlredyExists;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Cliente> save(@RequestBody ClienteRequestDTO dto){

        try{

            var cliente =  clienteService.save(dto);
            return new ResponseEntity<>(cliente, HttpStatus.CREATED);

        } catch (AccountAlredyExists e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    //TODO: Metodo deveria vir da conta e não do cliente
    @PostMapping("/{cpf}")
    public ResponseEntity<AprovarClienteDTO> aprovar(@PathVariable String cpf){
        return new ResponseEntity<>(clienteService.aprovar(cpf, null, null), HttpStatus.CREATED);
    }
    
    @PutMapping(value = "/{cpf}")
    public ResponseEntity<Cliente> update(@PathVariable String cpf, @RequestBody AlterarDadosClienteDTO cliente){
        return new ResponseEntity<>(clienteService.update(cliente, cpf), HttpStatus.OK);
    }

}        
        