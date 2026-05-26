package com.bantads.conta.repository.write;

import com.bantads.conta.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {

       @Query("SELECT m FROM Movimentacao m WHERE (m.contaOrigem = :conta OR m.contaDestino = :conta) " +
              "AND m.dataHora BETWEEN :inicio AND :fim ORDER BY m.dataHora ASC")
       List<Movimentacao> findByContaAndPeriodo(@Param("conta") String conta,
                                                 @Param("inicio") LocalDateTime inicio,
                                                 @Param("fim") LocalDateTime fim);

       @Query("SELECT m FROM Movimentacao m WHERE (m.contaOrigem = :conta OR m.contaDestino = :conta) " +
              "AND m.dataHora < :data")
       List<Movimentacao> findByContaBefore(@Param("conta") String conta,
                                          @Param("data") LocalDateTime data);

       @Query("SELECT MIN(m.dataHora), MAX(m.dataHora) FROM Movimentacao m WHERE m.contaOrigem = :conta OR m.contaDestino = :conta")
       LocalDateTime[] findFirstMovimentacao(@Param("conta") String conta);

       Optional<Movimentacao> findFirstByContaOrigemOrContaDestinoOrderByDataHoraAsc(String contaOrigem, String contaDestino);

       Optional<Movimentacao> findFirstByContaOrigemOrContaDestinoOrderByDataHoraDesc(String contaOrigem, String contaDestino);
}
