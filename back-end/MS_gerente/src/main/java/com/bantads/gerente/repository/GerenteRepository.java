package com.bantads.gerente.repository;

import com.bantads.gerente.model.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, UUID>, RevisionRepository<Gerente, UUID, Integer> {
    Optional<Gerente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    void deleteByCpf(String cpf);

    @Query(value = "SELECT * FROM tb_gerente WHERE tipo='GERENTE' ORDER BY CAST(total_clientes AS INTEGER) ASC LIMIT 1", nativeQuery = true)
    Optional<Gerente> findFirstByOrderByTotalClientesAsc();
} 
        