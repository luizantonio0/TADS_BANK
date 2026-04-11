package com.bantads.conta.repository;

import com.bantads.conta.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID> {

    boolean existsByCpf(String cpf);
    boolean existsByConta(String conta);
}
