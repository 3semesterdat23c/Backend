package org.example.backendclerkio.repository;

import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.Order;
import org.example.backendclerkio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderProducts WHERE o.user = :user AND o.paid = :paid")
    Optional<Order> findByUserAndPaidWithProducts(@Param("user") User user, @Param("paid") boolean paid);

    // Other methods...
}
