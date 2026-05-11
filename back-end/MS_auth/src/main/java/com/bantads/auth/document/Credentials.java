package com.bantads.auth.document;

import org.javers.spring.annotation.JaversAuditable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "credentials")
public class Credentials {
    @Id
    private String cpf;
    private String email;
    private String password;
    private String profile;

    public Credentials(String cpf, String email, String password, String profile) {
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public String getCpf() {
        return cpf;
    }
    
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
