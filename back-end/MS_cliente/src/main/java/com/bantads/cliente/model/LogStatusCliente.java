package com.bantads.cliente.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.envers.Audited;

import com.bantads.cliente.enums.LogStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_log_cliente")
@Audited
public class LogStatusCliente {

    @Id
    @Column
    private UUID id;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(length = 2)
    @Enumerated(EnumType.STRING)
    private LogStatus status;

    @Column
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime data;

    public LogStatusCliente(UUID id, String cpf, LogStatus status, String motivo, LocalDateTime data) {
        this.cpf = cpf;
        this.id = id;
        this.status = status;
        this.motivo = motivo;
        this.data = data;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LogStatus getStatus() {
        return status;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    

}