package org.example.backendclerkio.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
public record ProductsResponseDTO(
        @JsonProperty("products") List<ProductResponseDTO> products,
        @JsonProperty("total") int total,
        @JsonProperty("skip") int skip,
        @JsonProperty("limit") int limit
) {}

