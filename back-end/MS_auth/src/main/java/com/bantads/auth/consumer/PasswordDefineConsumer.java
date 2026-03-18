package com.bantads.auth.consumer;

import com.bantads.auth.dto.PasswordDefineDTO;
import com.bantads.auth.service.AuthService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PasswordDefineConsumer {

    @Autowired
    private AuthService authService;

    @RabbitListener(queues = "ms_auth.password_define")
    public void consumeMSAuthPasswordDefine(PasswordDefineDTO dto) {

    }


}
