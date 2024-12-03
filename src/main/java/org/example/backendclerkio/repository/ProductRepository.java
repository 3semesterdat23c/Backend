package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findAllBy(Pageable pageable);
    Page<Product> findByStockCountBetween(int min, int max, Pageable pageable);
    Page<Product> findByStockCount(int stock, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
