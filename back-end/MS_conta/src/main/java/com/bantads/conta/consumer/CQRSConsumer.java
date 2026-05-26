package com.bantads.conta.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.conta.datasource.DataSourceContextHolder;
import com.bantads.conta.datasource.DataSourceType;
import com.bantads.conta.dto.cqrs.CQRSSyncEntity;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.service.MovimentacaoService;

@Component
public class CQRSConsumer {

    @Autowired private ContaService contaService;
    @Autowired private MovimentacaoService movimentacaoService;
    
    @RabbitListener(queues = "ms-conta.cqrs.movimentacao")
    @Transactional(value = "readTransactionManager")
    public void onMovimentacaoSync(CQRSSyncEntity.MovimentacaoDTO dto) {
        DataSourceContextHolder.setContext(DataSourceType.READER);
        movimentacaoService.sync(dto);
    }

    @RabbitListener(queues = "ms-conta.cqrs.conta")
    @Transactional(value = "readTransactionManager")
    public void onContaSync(CQRSSyncEntity.ContaDTO dto) {
        DataSourceContextHolder.setContext(DataSourceType.READER);
        contaService.sync(dto);
    }

}
