package com.bantads.cliente.controller;

import com.bantads.cliente.dto.ClienteDTO;
import com.bantads.cliente.dto.RejeitarClienteRequestDTO;
import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.http.AprovarClienteDTO;
import com.bantads.cliente.dto.http.AprovarClienteResponseDTO;
import com.bantads.cliente.dto.http.ClienteCreateResponseDTO;
import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.exception.HttpException;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.service.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired private ClienteService clienteService;
    @Autowired private OrchestrationService orchestrationService;

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> findAll(
        @RequestParam(name = "filtro", required = false, defaultValue = "") String filtro,
        @RequestParam(name = "nome", required = false, defaultValue = "") String nome,
        @RequestHeader("X-User-Id") String cpfLogado,
        @RequestHeader("X-User-Profile") String profileLogado
    ) throws HttpException {
        return ResponseEntity.ok(clienteService.findClientes(cpfLogado, profileLogado, filtro, nome).stream().map(ClienteDTO::from).toList());
    }

    @GetMapping("/relation")
    public ResponseEntity<Map<String, List<ClienteDTO>>> findAll(
        @RequestParam(name = "gerentes", required = false, defaultValue = "") String gerentes
    ) throws HttpException {
        return ResponseEntity.ok(clienteService.findClientesByGerentes(gerentes));
    }

    @GetMapping("/gerente/{cpf}")
    public ResponseEntity<List<ClienteDTO>> findByGerente(@PathVariable("cpf") String cpf) throws HttpException {
        return ResponseEntity.ok(clienteService.findClientesByGerente(cpf).stream().map(c -> ClienteDTO.from(c)).toList());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ClienteDTO> findByCpf(@PathVariable("cpf") String cpf) throws HttpException {
        return ResponseEntity.ok(ClienteDTO.from(clienteService.findByCpf(cpf.replaceAll("[^0-9]", ""))));
    }

    @GetMapping("/nomes")
    public ResponseEntity<Map<String, String>> findNomesByCpf(
        @RequestParam(name = "filtro", required = false, defaultValue = "") String filtro
    ) throws HttpException {
        return ResponseEntity.ok(clienteService.findNomesByCpf(Arrays.asList(filtro.split(","))));
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ClienteCreateResponseDTO>> save(@RequestBody ClienteRequestDTO dto) throws Exception {    
        return orchestrationService.startCriarCliente(dto)
            .thenApply(ResponseEntity::ok)
            .orTimeout(30, TimeUnit.SECONDS);
    }

    @PostMapping("/{cpf}/aprovar")
    public CompletableFuture<ResponseEntity<AprovarClienteResponseDTO>> aprovar(@PathVariable("cpf") String cpf, @RequestHeader("X-User-Id") String cpfGerente) throws Exception {
        return orchestrationService.startAprovarCliente(cpfGerente, new AprovarClienteDTO(cpf.replaceAll("[^0-9]", "")))
                .thenApply(ResponseEntity::ok)
                .orTimeout(30, TimeUnit.SECONDS);
    }

    @PostMapping("/{cpf}/rejeitar")
    public CompletableFuture<ResponseEntity<Object>> rejeitar(
        @PathVariable("cpf") String cpf, 
        @RequestHeader("X-User-Id") String cpfGerente,
        @RequestBody RejeitarClienteRequestDTO dto
    ) throws Exception {
        return orchestrationService.startRejeitarCliente(cpfGerente, cpf, dto)
                .thenApply(ResponseEntity::ok)
                .orTimeout(30, TimeUnit.SECONDS);
    }


    @PutMapping(value = "/{cpf}")
    public CompletableFuture<ResponseEntity<Object>> update(
        @PathVariable("cpf") String cpf,
        @RequestHeader("X-User-Id") String cpfLogado,
        @RequestHeader("X-User-Profile") String profileLogado,
        @RequestBody AlterarDadosClienteDTO dto
    ) throws Exception{
        var cpfNormalizado = cpf.replaceAll("[^0-9]", "");

        if (profileLogado.equalsIgnoreCase("CLIENTE") && !cpfNormalizado.equals(cpfLogado)) {
            throw new com.bantads.cliente.exception.ForbiddenException("Voce nao tem permissao para alterar este cliente.");
        }

        return orchestrationService.startAtualizarCliente(cpfNormalizado, dto)
                .thenApply(ResponseEntity::ok)
                .orTimeout(15, TimeUnit.HOURS);
    }

}        
        
