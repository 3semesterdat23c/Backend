package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserEmail(String email);
    Optional<User> findByUserId(int id);
    boolean existsByUserEmail(String email);
}