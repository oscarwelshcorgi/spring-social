package com.example.springsocial.repository;

import com.example.springsocial.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmailAndPassword(String email, String password);
    Optional<Users> findByEmail(String email); // 이미 생성된 사용자인지 체크(email 기준으로 체크)
    boolean existsUsersByEmail(String email);
}
