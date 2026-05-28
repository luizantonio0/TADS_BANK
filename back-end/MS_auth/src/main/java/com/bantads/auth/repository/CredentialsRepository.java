package com.bantads.auth.repository;

import com.bantads.auth.document.Credentials;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface CredentialsRepository extends MongoRepository<Credentials, String> {
    Optional<Credentials> findByEmail(@Param("email") String email);
    Optional<Credentials> findByCpf(@Param("cpf") String cpf);
    boolean existsByEmail(String email);
}
