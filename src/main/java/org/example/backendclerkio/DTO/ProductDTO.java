package org.example.backendclerkio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

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
