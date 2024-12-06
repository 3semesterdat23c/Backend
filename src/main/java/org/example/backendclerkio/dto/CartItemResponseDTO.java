package org.example.backendclerkio.dto;

public record CartItemResponseDTO(int productId,
                                  String productName,
                                  int quantity,
                                  double priceAtTimeOfOrder,
                                  double originalPrice,
                                  String productImageUrl) {}
