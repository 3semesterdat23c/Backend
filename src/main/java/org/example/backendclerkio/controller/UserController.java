package org.example.backendclerkio.controller;

import org.example.backendclerkio.dto.LoginRequestDTO;
import org.example.backendclerkio.dto.UserRequestDTO;
import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public UserResponseDTO getUser(@RequestParam int userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        boolean creationSuccesful = userService.registerUser(userRequestDTO);

        if (creationSuccesful) {
            return ResponseEntity.ok("User created succesfully");
        } else {
            return ResponseEntity.status(409).body("User with email already exists");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        boolean isAuthenticated = userService.loginUser(loginRequestDTO);

        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestParam int userId, @RequestBody UserRequestDTO userRequestDTO) {
        boolean updateSuccesful = userService.updateUser(userId, userRequestDTO);

        if (updateSuccesful) {
            return ResponseEntity.ok("User updated");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        boolean deletionSuccesful = userService.deleteUser(userId);

        if (deletionSuccesful) {
            return ResponseEntity.ok("User deleted succesfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}