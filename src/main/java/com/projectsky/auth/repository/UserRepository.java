package com.projectsky.auth.repository;

import com.projectsky.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(UUID id);

    Optional<User> findByEmail(String email);
}
