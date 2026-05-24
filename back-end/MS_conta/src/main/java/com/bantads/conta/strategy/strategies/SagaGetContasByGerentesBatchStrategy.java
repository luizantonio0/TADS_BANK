package com.bantads.conta.strategy.strategies;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bantads.conta.dto.ContaDTO;
import com.bantads.conta.dto.GetContasByGerentesBatchOutputDTO;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaGetContasByGerentesBatchStrategy implements SagaCommandStrategy {

  @Autowired
  private ContaService contaService;

  @Override
  public Object handle(OrchestrationCommandDTO cmd) throws Exception {
    var mapper = new ObjectMapper();
    
    var dto = mapper.readValue(cmd.payload(), new TypeReference<List<String>>(){});
    var response = new HashMap<String, GetContasByGerentesBatchOutputDTO>();

    for (String gerente : dto) {
      var contas = contaService.findByGerente(gerente).stream().collect(Collectors.toMap(c->c.getCpf(), ContaDTO::from));
      var saldoNegativo = contaService.findSumSaldoNegativo(gerente);
      var saldoPositivo = contaService.findSumSaldoPositivo(gerente);
      var output = new GetContasByGerentesBatchOutputDTO(contas, saldoPositivo, saldoNegativo);
      response.put(gerente, output);
    }

    return response;
  }

}
