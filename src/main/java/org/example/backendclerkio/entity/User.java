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

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String userEmail;

    @Column(name = "isAdmin", nullable = false)
    private boolean isAdmin;

    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

    public User(String firstName, String lastName, String userEmail, String passwordHash) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.isAdmin = false;
        this.passwordHash = passwordHash;
    }
}