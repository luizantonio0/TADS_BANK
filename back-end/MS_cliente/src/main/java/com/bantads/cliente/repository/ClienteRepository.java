package com.bantads.cliente.repository;

import com.bantads.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID>, RevisionRepository<Cliente, UUID, Integer> {

    Optional<Cliente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    List<Cliente> findByCpfGerenteAndAprovado(String cpfGerente, boolean aprovado);

}
        