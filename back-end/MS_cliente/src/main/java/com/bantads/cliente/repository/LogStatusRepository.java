package com.bantads.cliente.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import com.bantads.cliente.model.LogStatusCliente;

public interface LogStatusRepository extends JpaRepository<LogStatusCliente, UUID>, RevisionRepository<LogStatusCliente, UUID, Integer>{
    boolean existsByCpf(String cpf);
    Optional<LogStatusCliente> findByCpf(String cpf);
}
