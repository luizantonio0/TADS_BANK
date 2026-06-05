package com.bantads.gerente.model;

import com.bantads.gerente.dto.request.CriaGerenteDTO;
import jakarta.persistence.*;
import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.enums.GerenteTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_gerente", schema = "public")
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
    private String tipo;

    @Column(nullable = false, length = 11)
    private String telefone;

    @Column(nullable = false)
    private Integer totalClientes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "tb_gerente_clientes",
        joinColumns = @JoinColumn(name = "gerente_id")
    )
    @Column(name = "cpf")
    private List<String> clientes = new ArrayList<>();

    public Gerente() {
    }

    public Gerente(AtualizaGerenteDTO atualizaGerenteDTO) {
        this.nome = atualizaGerenteDTO.nome();
        this.email = atualizaGerenteDTO.email();
    }

    public Gerente(CriaGerenteDTO criaGerenteDTO, List<String> clientes) {
        this.nome = criaGerenteDTO.nome();
        this.cpf = criaGerenteDTO.cpf();
        this.email = criaGerenteDTO.email();
        this.tipo = criaGerenteDTO.tipo().name();
        this.totalClientes = 0;
        this.clientes = clientes;
        this.telefone = criaGerenteDTO.telefone();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
      this.telefone = telefone;
    }

    public void incrementTotalClientes() {
        this.totalClientes++;
    }

    public void decrementTotalClientes() {
        this.totalClientes = Math.max(0, this.totalClientes-1);
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

    public String getTipo() {
        return tipo;
    }

    public Integer getTotalClientes() {
        return totalClientes;
    }

    public List<String> getClientes() {
      return clientes;
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

    public void setTotalClientes(Integer totalClientes) {
        this.totalClientes = totalClientes;
    }

    public void setClientes(List<String> clientes) {
        this.clientes = clientes;
    }
}
        