package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);

    User findByUserEmail(String email);
    boolean existsByUserEmail(String email);
}