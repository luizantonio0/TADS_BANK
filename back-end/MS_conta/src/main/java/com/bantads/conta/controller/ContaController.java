package com.bantads.conta.controller;

import com.bantads.conta.dto.*;
import com.bantads.conta.exception.HttpException;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bantads.conta.datasource.DataSourceType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @GetMapping
    public ResponseEntity<List<ContaDTO>> findAll(
        @RequestParam(name = "filtro", required = false) String filtro,
        @RequestParam(name = "contas", required = false) String contas,
        @RequestHeader("X-User-Id") String cpfLogado
    ) throws Exception {
        return new ResponseEntity<>(contaService.findAll(filtro, cpfLogado, contas).stream().map(ContaDTO::from).toList(), HttpStatus.OK);
    }

    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<ContaDTO> findByCliente(@PathVariable("cpf") String cpf) throws HttpException {
        return ResponseEntity.ok(ContaDTO.from(contaService.findByCpf(cpf)));
    }

    @GetMapping("/relation")
    public ResponseEntity<Map<String, ContaDTO>> findByGerente(
            @RequestParam(name = "gerentes", required = false, defaultValue = "") String gerentes)
            throws HttpException {
        return ResponseEntity.ok(contaService.findContasByGerentes(gerentes));
    }

    @GetMapping("/saldos")
    public ResponseEntity<Map<String, SaldoGerenteDTO>> findSaldosByGerente(
            @RequestParam(name = "gerentes", required = false, defaultValue = "") String gerentes)
            throws HttpException {
        return ResponseEntity.ok(contaService.findSaldosByGerentes(gerentes));
    }

    @PostMapping("/{conta}/depositar")
    public ResponseEntity<Void> depositar(
            @RequestHeader("X-User-Id") String cpfLogado,
            @PathVariable("conta") String conta,
            @RequestBody DepositoDTO dto) throws HttpException {
        movimentacaoService.depositar(conta, cpfLogado, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{conta}/sacar")
    public ResponseEntity<Void> sacar(
            @PathVariable("conta") String conta,
            @RequestBody SaqueDTO dto) {
        movimentacaoService.sacar(conta, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{conta}/transferir")
    public ResponseEntity<Void> transferir(
            @RequestHeader("X-User-Id") String cpfLogado,
            @PathVariable("conta") String conta,
            @RequestBody TransferenciaDTO dto) throws HttpException {
        movimentacaoService.transferir(conta, cpfLogado, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{numConta}")
    public ResponseEntity<Object> getSaldo(@PathVariable String numConta) {
        return ResponseEntity.ok(contaService.getConta(numConta));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ContaDTO> getContaByCpf(@PathVariable String cpf) throws HttpException {
        return ResponseEntity.ok(ContaDTO.from(contaService.findByCpf(cpf)));
    }

    @GetMapping("/{numConta}/extrato")
    public ResponseEntity<ExtratoResponseDTO> getExtrato(
            @PathVariable String numConta,
            @RequestParam(name = "include", required = false) String include,
            @RequestParam(name = "inicio", required = false, defaultValue = "1971-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false, defaultValue = "2100-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) throws HttpException{
        return ResponseEntity.ok(movimentacaoService.getExtrato(numConta, inicio, fim));
    }

    @GetMapping("/reboot")
    public ResponseEntity<?> reboot() {
        contaService.reboot(DataSourceType.READER);
        contaService.reboot(DataSourceType.WRITER);
        movimentacaoService.reboot(DataSourceType.READER);
        movimentacaoService.reboot(DataSourceType.WRITER);
        return ResponseEntity.ok("Banco de dados criado conforme especificação");
    }

}
