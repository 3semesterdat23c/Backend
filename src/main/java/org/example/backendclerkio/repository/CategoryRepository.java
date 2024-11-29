package org.example.backendclerkio.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.example.backendclerkio.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryName(String name);
}
