package org.example.backendclerkio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String userEmail;

    @Column(name = "isAdmin", nullable = false)
    private boolean isAdmin;

    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

    public User(String username, String userEmail, String passwordHash) {
        this.username = username;
        this.userEmail = userEmail;
        this.isAdmin = false;
        this.passwordHash = passwordHash;
    }
}