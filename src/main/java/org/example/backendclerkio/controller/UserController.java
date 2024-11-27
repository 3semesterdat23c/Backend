package org.example.backendclerkio.controller;

import org.example.backendclerkio.JwtTokenManager;
import org.example.backendclerkio.dto.JwtResponseModelDTO;
import org.example.backendclerkio.dto.LoginRequestDTO;
import org.example.backendclerkio.dto.UserRequestDTO;
import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.service.JwtUserDetailsService;
import org.example.backendclerkio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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