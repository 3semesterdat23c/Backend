package org.example.backendclerkio.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
public record ProductsResponseDTO(
        @JsonProperty("products") List<ProductDTO> products,
        @JsonProperty("total") int total,
        @JsonProperty("skip") int skip,
        @JsonProperty("limit") int limit
) {}

