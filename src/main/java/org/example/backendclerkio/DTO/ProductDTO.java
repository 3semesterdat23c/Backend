package org.example.backendclerkio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProductDTO(
        @JsonProperty("id") int id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("price") float price,
        @JsonProperty("discountPercentage") float discountPercentage,
        @JsonProperty("rating") float rating,
        @JsonProperty("stock") int stock,
        @JsonProperty("brand") String brand,
        @JsonProperty("category") String category,
        @JsonProperty("thumbnail") String thumbnail,
        @JsonProperty("images") List<String> images
) {}
