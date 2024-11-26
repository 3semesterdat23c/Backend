package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
