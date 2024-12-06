package org.example.backendclerkio.dto;

public record PaymentRequestDTO(long cardNumber, String cardHolder, int expiryDate, int cvv
                                ) {
}
