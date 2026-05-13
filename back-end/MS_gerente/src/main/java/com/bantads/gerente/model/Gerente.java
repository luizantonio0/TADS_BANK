package com.bantads.gerente.model;

import com.bantads.gerente.dto.request.CriaGerenteDTO;
import jakarta.persistence.*;
import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.enums.GerenteTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Entity
@Table(name = "tb_gerente")
@Audited
public class Gerente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 11)
    private String cpf;

    @NotBlank
    @Size(min = 3, max = 20)
    @Column(name = "nome", nullable = false, length = 20)
    private String nome;

    @NotBlank
    @Size(min = 3, max = 128)
    @Column(name = "email", nullable = false, length = 128)
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Senha não pode ser vazio")
    private String senha;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Integer totalClientes;

    public Gerente() {
    }

    public Gerente(AtualizaGerenteDTO atualizaGerenteDTO) {
        this.nome = atualizaGerenteDTO.nome();
        this.email = atualizaGerenteDTO.email();
        this.senha = atualizaGerenteDTO.senha();
    }

    public Gerente(CriaGerenteDTO criaGerenteDTO) {
        this.nome = criaGerenteDTO.nome();
        this.cpf = criaGerenteDTO.cpf();
        this.email = criaGerenteDTO.email();
        this.senha = criaGerenteDTO.senha();
        this.tipo = criaGerenteDTO.tipo().name();
        this.totalClientes = 0;
    }

    public void incrementTotalClientes() {
        this.totalClientes++;
    }

    public void setTipo(GerenteTipo tipo) {
        this.tipo = tipo.name();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getTotalClientes() {
        return totalClientes;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setTotalClientes(Integer totalClientes) {
        this.totalClientes = totalClientes;
    }
}
        