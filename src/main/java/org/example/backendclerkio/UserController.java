package org.example.backendclerkio;

import org.example.backendclerkio.dto.LoginRequestDTO;
import org.example.backendclerkio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}