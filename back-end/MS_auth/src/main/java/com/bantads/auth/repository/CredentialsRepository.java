package com.bantads.auth.repository;

import com.bantads.auth.document.Credentials;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CredentialsRepository extends MongoRepository<Credentials, String> {
    Optional<Credentials> findByEmail(String email);
}
