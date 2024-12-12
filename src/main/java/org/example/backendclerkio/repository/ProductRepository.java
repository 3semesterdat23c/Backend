package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findAllBy(Pageable pageable);
    Page<Product> findAllByCategory(Pageable pageable, Category category);



    Page<Product> findProductsByCategory_CategoryName(String categoryCategoryName, Pageable pageable);
    Page<Product> findByStockCountBetween(int min, int max, Pageable pageable);
    Page<Product> findByStockCount(int stock, Pageable pageable);
    Page<Product> findByTitleContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE (:category IS NULL OR p.category.categoryName = :category) " +
            "AND (:search = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:minPrice IS NULL OR p.discountPrice >= :minPrice) " + // New Filter
            "AND (:maxPrice IS NULL OR p.discountPrice <= :maxPrice) " + // New Filter
            "AND ( " +
            "   (:lowStock = false AND :outOfStock = false) " +
            "   OR (:lowStock = true AND p.stockCount > 0 and p.stockCount <= 5) " +
            "   OR (:outOfStock = true AND p.stockCount = 0) " +
            ")")
    Page<Product> findProducts(
            @Param("category") String category,
            @Param("search") String search,
            @Param("lowStock") boolean lowStock,
            @Param("outOfStock") boolean outOfStock,
            @Param("minPrice") Integer minPrice, // New Parameter
            @Param("maxPrice") Integer maxPrice, // New Parameter
            Pageable pageable
    );

}
