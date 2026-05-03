package com.bantads.conta.controller;

import com.bantads.conta.dto.*;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private MovimentacaoService movimentacaoService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody ContaCreateInputDTO dto) throws Exception {
        return new ResponseEntity<>(contaService.createConta(dto), HttpStatus.CREATED);
    }

    @PostMapping("/deposito")
    public ResponseEntity<Void> depositar(@RequestBody DepositoDTO dto) {
        movimentacaoService.depositar(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saque")
    public ResponseEntity<Void> sacar(@RequestBody SaqueDTO dto) {
        movimentacaoService.sacar(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transferencia")
    public ResponseEntity<Void> transferir(@RequestBody TransferenciaDTO dto) {
        movimentacaoService.transferir(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{numConta}")
    public ResponseEntity<Object> getSaldo(@PathVariable String numConta) {
        return ResponseEntity.ok(contaService.getConta(numConta));
    }

    @GetMapping("/{numConta}/extrato")
    public ResponseEntity<ExtratoResponseDTO> getExtrato(
            @PathVariable String numConta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(movimentacaoService.getExtrato(numConta, inicio, fim));
    }

}
