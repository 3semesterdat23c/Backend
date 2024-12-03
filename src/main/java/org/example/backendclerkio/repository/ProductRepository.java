package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findAllBy(Pageable pageable);

    Page<Product> findProductsByCategory_CategoryName(String categoryCategoryName, Pageable pageable);
}
