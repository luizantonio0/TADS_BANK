package com.bantads.auth.repository;

import com.bantads.auth.document.Credentials;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends MongoRepository<Credentials, String> {
    Optional<Credentials> findByEmail(String email);
}
