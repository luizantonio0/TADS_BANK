package com.bantads.conta.repository.read;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.conta.model.Conta;

public interface ContaReadRepository extends JpaRepository<Conta, UUID> {
  
}
