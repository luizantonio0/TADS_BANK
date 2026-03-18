package com.bantads.auth.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public boolean login(String login, String senha) {
        return login.equals("victor@gmail.com") && senha.equals("1234");
    }

}
