package org.example.backendclerkio.repository;

import org.example.backendclerkio.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    boolean existsByTagName(String tagName);
    Optional<Tag> findByTagName(String tagName);
}
