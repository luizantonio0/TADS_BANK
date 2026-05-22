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

    @Query("SELECT g FROM Gerente g WHERE g.tipo = 'GERENTE' ORDER BY g.totalClientes ASC")
    Optional<Gerente> findTop1GerenteComMenosClientes();
} 
        