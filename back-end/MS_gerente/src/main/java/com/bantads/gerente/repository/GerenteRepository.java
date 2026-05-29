package com.bantads.gerente.repository;

import com.bantads.gerente.model.Gerente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, UUID>, RevisionRepository<Gerente, UUID, Integer> {
    Optional<Gerente> findByCpf(String cpf);
    Optional<Gerente> findByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    void deleteByCpf(String cpf);

    @Query("SELECT g FROM Gerente g WHERE g.totalClientes = (SELECT MAX(g2.totalClientes) FROM Gerente g2)")
    List<Gerente> findGerentesComMaisClientes();

    List<Gerente> findByTipoOrderByNomeAsc(String tipo);

    @Query("SELECT g FROM Gerente g WHERE g.tipo = 'GERENTE' ORDER BY g.totalClientes ASC, g.cpf ASC")
    List<Gerente> findTop1GerenteComMenosClientes();
} 
        