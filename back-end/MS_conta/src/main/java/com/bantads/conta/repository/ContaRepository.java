package com.bantads.conta.repository;

import com.bantads.conta.model.Conta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID>, RevisionRepository<Conta, UUID, Integer> {

    boolean existsByCpf(String cpf);
    boolean existsByConta(String conta);
    Optional<Conta> findByConta(String conta);
    Optional<Conta> findByCpf(String cpf);
    List<Conta> findByCpfIn(List<String> cpf);
    List<Conta> findByCpfGerente(String cpfGerente);
    List<Conta> findByCpfGerenteIn(List<String> cpfGerente);
    List<Conta> findTop3ByOrderBySaldoDesc();

    @Query("SELECT COALESCE(SUM(c.saldo), 0) FROM Conta c WHERE c.cpfGerente = :cpfGerente AND c.saldo < 0")
    BigDecimal sumSaldosNegativosByCpfGerente(@Param("cpfGerente") String cpfGerente);

    @Query("SELECT COALESCE(SUM(c.saldo), 0) FROM Conta c WHERE c.cpfGerente = :cpfGerente AND c.saldo >= 0")
    BigDecimal sumSaldosPositivosByCpfGerente(@Param("cpfGerente") String cpfGerente);
}
