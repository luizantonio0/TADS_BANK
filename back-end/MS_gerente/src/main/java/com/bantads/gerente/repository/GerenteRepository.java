package com.bantads.gerente.repository;

import com.bantads.gerente.model.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, UUID>, RevisionRepository<Gerente, UUID, Integer> {
    Gerente findByCpf(String cpf);
    void deleteByCpf(String cpf);

    Optional<Gerente> findFirstByOrderByTotalClientesAsc();
} 
        