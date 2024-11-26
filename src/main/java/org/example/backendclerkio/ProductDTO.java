package org.example.backendclerkio;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProductDTO(
        @JsonProperty("id") int id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("price") float price,
        @JsonProperty("stock") int stock,
        @JsonProperty("category") String category,
        @JsonProperty("images") List<String> images
) {}
