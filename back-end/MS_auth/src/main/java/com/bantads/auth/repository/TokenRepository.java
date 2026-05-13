package com.bantads.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bantads.auth.document.Token;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {
    Optional<Token> findByToken(String token);
    boolean existsByToken(String token);
    void deleteByToken(String token);
    void deleteByCpf(String cpf);
}
