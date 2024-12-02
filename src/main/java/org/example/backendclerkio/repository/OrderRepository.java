package org.example.backendclerkio.repository;

import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.Order;
import org.example.backendclerkio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Integer> {
  
    Optional<Order> findByUserAndPaid(User user, boolean paid);

}
