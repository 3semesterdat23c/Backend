package org.example.backendclerkio.controller;

import org.example.backendclerkio.JwtTokenManager;
import org.example.backendclerkio.dto.JwtResponseModelDTO;
import org.example.backendclerkio.dto.LoginRequestDTO;
import org.example.backendclerkio.dto.UserRequestDTO;
import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.service.JwtUserDetailsService;
import org.example.backendclerkio.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenManager jwtTokenManager;


    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUserDetailsService jwtUserDetailsService, JwtTokenManager jwtTokenManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenManager = jwtTokenManager;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable int userId) {
        return userService.getUser(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        if (userService.userExistsByEmail(userRequestDTO.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A user with the email " + userRequestDTO.email() + " already exists.");
        }

        UserResponseDTO userResponseDTO = userService.registerUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseModelDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.email(),
                            loginRequestDTO.password())
            );
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            return ResponseEntity.ok(new JwtResponseModelDTO("bad credentials"));
        }
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(loginRequestDTO.email());
        final String jwtToken = jwtTokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModelDTO(jwtToken));
    }

    @PutMapping("users/{userId}/update")
    public ResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody UserRequestDTO userRequestDTO) {
        if (!userService.userExistsByUserId(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with user ID: " + userId + " not found.");
        }

        Optional<UserResponseDTO> updatedUser = userService.updateUser(userId, userRequestDTO);
        if (updatedUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedUser.get());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update the user. Please try again.");
        }
    }

    @DeleteMapping("users/{userId}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        if (!userService.userExistsByUserId(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with user ID: " + userId + " not found.");
        }

        if (userService.deleteUser(userId)) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete user. Please try again.");
        }
    }
}