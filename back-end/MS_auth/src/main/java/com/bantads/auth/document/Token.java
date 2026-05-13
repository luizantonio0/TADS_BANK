package com.bantads.auth.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tokens")
public class Token {

    @Id
    @Indexed(unique = true)
    private String token;

    private String cpf;

    public Token(String cpf, String token) {
        this.cpf = cpf;
        this.token = token;
    }

    public String getCpf() {
        return cpf;
    }

    public String getToken() {
        return token;
    }    

}