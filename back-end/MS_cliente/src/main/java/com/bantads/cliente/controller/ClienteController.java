package com.bantads.cliente.controller;

import com.bantads.cliente.dto.ClienteDTO;
import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.http.AprovarClienteDTO;
import com.bantads.cliente.dto.http.AprovarClienteResponseDTO;
import com.bantads.cliente.dto.http.ClienteCreateResponseDTO;
import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.exception.NotFoundException;
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
    public ResponseEntity<ClienteDTO> findByCpf(@PathVariable("cpf") String cpf) throws NotFoundException {
        return new ResponseEntity<>(ClienteDTO.from(clienteService.findByCpf(cpf)), HttpStatus.OK);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ClienteCreateResponseDTO>> save(@RequestBody ClienteRequestDTO dto) throws Exception {    
        return orchestrationService.startCriarCliente(dto)
            .thenApply(ResponseEntity::ok)
            .orTimeout(15, TimeUnit.SECONDS);
    }

    @PostMapping("/{cpf}/aprovar")
    public CompletableFuture<ResponseEntity<AprovarClienteResponseDTO>> aprovar(@PathVariable("cpf") String cpf, @RequestHeader("X-User-Id") String cpfGerente) throws Exception {
        return orchestrationService.startAprovarCliente(cpfGerente, new AprovarClienteDTO(cpf.replaceAll("[^0-9]", "")))
                .thenApply(ResponseEntity::ok)
                .orTimeout(30, TimeUnit.SECONDS);
    }


    @PutMapping(value = "/{cpf}")
    public CompletableFuture<ResponseEntity<Object>> update(@PathVariable("cpf") String cpf, @RequestBody AlterarDadosClienteDTO dto) throws Exception{
        return orchestrationService.startAtualizarCliente(cpf.replaceAll("[^0-9]", ""), dto)
                .thenApply(ResponseEntity::ok)
                .orTimeout(15, TimeUnit.HOURS);
    }

}        
        