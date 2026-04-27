package com.bantads.cliente.model;

import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.enums.UF;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_cliente")
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
    private UUID idGerente;

    public Cliente() {
    }

    public Cliente(ClienteRequestDTO clienteRequestDTO) {
        this.cpf = clienteRequestDTO.cpf().replaceAll("[^0-9]", "");
        this.email = clienteRequestDTO.email();
        this.nome = clienteRequestDTO.nome();
        this.telefone = clienteRequestDTO.telefone();
        this.salario = clienteRequestDTO.salario();
        this.cep = clienteRequestDTO.CEP();
        this.cidade = clienteRequestDTO.cidade();
        this.estado = clienteRequestDTO.estado();
        this.aprovado = false;
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

    public UF getEstado() {
        return estado;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setGerente(UUID id) {
        this.idGerente = id;
    }

    public String getEndereco() {
        return endereco;
    }
}
        