package com.example.authorizationserver.repository;

import com.example.authorizationserver.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String email);
    boolean existsByNickname(String nickname);
}
