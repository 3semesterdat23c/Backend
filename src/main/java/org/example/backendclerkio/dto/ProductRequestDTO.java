package org.example.backendclerkio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProductRequestDTO(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("price") float price,
        @JsonProperty("stock") int stock,
        @JsonProperty("category") String category,
        @JsonProperty("discountPercentage") float discountPercentage,
        @JsonProperty("images") List<String> images
) {}
