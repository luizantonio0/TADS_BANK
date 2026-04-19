package main.java.MS_Gerente.bantads.model;

import jakarta.persistence.*;
import main.java.MS_Gerente.bantads.dto.request.AtualizaGerenteDTO;
import main.java.MS_Gerente.bantads.enums.GerenteTipo;

import java.util.UUID;

@Entity
@Table(name = "tb_gerente")
public class Gerente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 20)
    private String nome;
    @Column(nullable = false, length = 11)
    private String cpf;
    @Column(nullable = false, length = 128)
    private String email;
    @Column(nullable = false)
    private String senha;
    @Column(nullable = false)
    private String tipo;

    public Gerente() {
    }
    public Gerente(AtualizaGerenteDTO atualizaGerenteDTO) {
        this.nome = atualizaGerenteDTO.nome();
        this.email = atualizaGerenteDTO.email();
        this.senha = atualizaGerenteDTO.senha();
    }
    public Gerente(String nome, String cpf, String email, String senha, String tipo) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(GerenteTipo tipo) {
        this.tipo = tipo.name();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
        