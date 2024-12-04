package org.example.backendclerkio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;

public record ProductResponseDTO(
        @JsonProperty("id") int id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("price") double price,
        @JsonProperty("discountPrice") double discountPrice,
        @JsonProperty("stock") int stockCount,
        @JsonProperty("category") String category, // Treat category as a string
        @JsonProperty("images") List<String> images,
        @JsonProperty("tags") Set<String> tags // Use Set<String> for tags
) {}