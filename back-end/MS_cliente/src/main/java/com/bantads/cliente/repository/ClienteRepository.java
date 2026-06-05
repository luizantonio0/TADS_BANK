package com.bantads.cliente.repository;

import com.bantads.cliente.model.Cliente;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
       List<Cliente> findByCpfGerenteAndAprovadoOrderByCriacaoAsc(String cpfGerente, boolean aprovado);
       List<Cliente> findByCpfIn(List<String> cpf);
       @Query("SELECT c FROM Cliente c WHERE c.cpfGerente = :cpfGerente " +
              "AND (c.cpf LIKE %:termo% OR c.nome LIKE %:termo%) AND c.aprovado = true " +
              "ORDER BY c.nome ASC")
       List<Cliente> findByGerente(@Param("cpfGerente") String cpfGerente, 
                                                 @Param("termo") String termo);

}
        