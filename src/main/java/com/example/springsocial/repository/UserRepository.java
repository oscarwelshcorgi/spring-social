package com.example.springsocial.repository;

import com.example.springsocial.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmailAndPassword(String email, String password);
    Optional<Users> findByEmail(String email);
    boolean existsUsersByEmail(String email);
}
