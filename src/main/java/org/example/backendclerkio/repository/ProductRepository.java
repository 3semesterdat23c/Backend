package org.example.backendclerkio.repository;

import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
