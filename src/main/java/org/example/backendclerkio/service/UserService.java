package org.example.backendclerkio.service;

import org.example.backendclerkio.config.SecurityConfiguration;
import org.example.backendclerkio.dto.LoginRequestDTO;
import org.example.backendclerkio.UserRequestDTO;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByUserEmail(userRequestDTO.email())) {
            return false;
        }

        PasswordEncoder passwordEncoder = SecurityConfiguration.passwordEncoder();
        User userEntity = new User(
                userRequestDTO.username(),
                userRequestDTO.email(),
                passwordEncoder.encode(userRequestDTO.password()));

        userRepository.save(userEntity);
        return true;
    }

    public List<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean loginUser(LoginRequestDTO loginRequestDTO) {
        var user = userRepository.findByUserEmail(loginRequestDTO.email());

        if (user != null) {
            String storedHashedPassword = user.getPasswordHash();
            return passwordEncoder.matches(loginRequestDTO.password(), storedHashedPassword);
        }

        return false; // User not found
    }
}