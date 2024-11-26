package org.example.backendclerkio.Entity;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    private String username;
    private String userEmail;
    private boolean isAdmin;
    private String passwordHash;
}
