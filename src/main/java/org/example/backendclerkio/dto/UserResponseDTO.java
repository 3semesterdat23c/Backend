package org.example.backendclerkio.dto;

public record UserResponseDTO(int userId, String firstName, String lastName, boolean isAdmin, String email) {
}
