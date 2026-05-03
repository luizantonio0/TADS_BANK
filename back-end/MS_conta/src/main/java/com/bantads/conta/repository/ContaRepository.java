package com.bantads.conta.repository;

import com.bantads.conta.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID>, RevisionRepository<Conta, UUID, Integer> {

    boolean existsByCpf(String cpf);
    boolean existsByConta(String conta);
    Optional<Conta> findByConta(String conta);
}
