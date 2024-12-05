package org.example.backendclerkio.dto;

public class ApiResponse {
    private String message;

    // Constructor
    public ApiResponse(String message) {
        this.message = message;
    }

    // Getter and Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
