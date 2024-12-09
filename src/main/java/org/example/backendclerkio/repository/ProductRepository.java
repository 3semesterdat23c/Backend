package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findAllBy(Pageable pageable);
    Page<Product> findAllByCategory(Pageable pageable, Category category);

    Page<Product> findProductsByCategory_CategoryName(String categoryCategoryName, Pageable pageable);
    Page<Product> findByStockCountBetween(int min, int max, Pageable pageable);
    Page<Product> findByStockCount(int stock, Pageable pageable);
    Page<Product> findByTitleContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByCategory(String category, Pageable pageable);


    @Query("SELECT p FROM Product p WHERE (:lowStock = true AND p.stockCount < 5) OR (:outOfStock = true AND p.stockCount = 0)")
    Page<Product> findFiltered(boolean lowStock, boolean outOfStock, Pageable pageable);

}
