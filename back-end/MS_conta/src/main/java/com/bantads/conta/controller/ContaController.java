package com.bantads.conta.controller;

import com.bantads.conta.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping("/{cpf}")
    public ResponseEntity<String> aprovar(@PathVariable String cpf){
        return new ResponseEntity<>(contaService.aprovar(cpf, null, null), HttpStatus.CREATED);
    }

}
