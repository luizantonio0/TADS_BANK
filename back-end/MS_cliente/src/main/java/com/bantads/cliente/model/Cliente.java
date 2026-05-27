package com.bantads.cliente.model;

import com.bantads.cliente.dto.http.ClienteRequestDTO;
import com.bantads.cliente.enums.UF;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_cliente", schema = "public")
@Audited
public class Cliente {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 30)
    private String nome;

    @Column(length = 11)
    private String telefone;

    private BigDecimal salario;

    @Column
    private String endereco;

    @Column(length = 8)
    private String cep;

    @Column(length = 30)
    private String cidade;

    @Column(length = 2)
    @Enumerated(EnumType.STRING)
    private UF estado;

    @Column
    private boolean aprovado;

    @Column
    private String cpfGerente;

    @Column
    private LocalDateTime criacao;

    public Cliente() {
    }

    public Cliente(ClienteRequestDTO clienteRequestDTO) {
        this.cpf = clienteRequestDTO.cpf().replaceAll("[^0-9]", "");
        this.email = clienteRequestDTO.email();
        this.nome = clienteRequestDTO.nome();
        this.telefone = clienteRequestDTO.telefone().replaceAll("[^0-9]", "");
        this.salario = clienteRequestDTO.salario();
        this.cep = clienteRequestDTO.CEP().replaceAll("[^0-9]", "");
        this.cidade = clienteRequestDTO.cidade();
        this.estado = clienteRequestDTO.estado();
        this.endereco = clienteRequestDTO.endereco();
        this.aprovado = false;
        this.criacao = LocalDateTime.now();
    }

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public boolean isAprovado() {
        return aprovado;
    }

    public void setAprovado(boolean aprovado) {
        this.aprovado = aprovado;
    }

    public UUID getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public String getCep() {
        return cep;
    }

    public String getCidade() {
        return cidade;
    }

    public String getCpfGerente() {
        return cpfGerente;
    }

    public UF getEstado() {
        return estado;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCpfGerente(String cpfGerente) {
        this.cpfGerente = cpfGerente;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setEstado(UF estado) {
        this.estado = estado;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    
}
        