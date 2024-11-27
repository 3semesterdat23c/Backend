package org.example.backendclerkio.service;

import org.example.backendclerkio.config.SecurityConfiguration;
import org.example.backendclerkio.dto.LoginRequestDTO;
import org.example.backendclerkio.dto.UserRequestDTO;
import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserResponseDTO> getUser(int userId) {
        return userRepository.findById(userId)
                .map(user -> new UserResponseDTO(
                        user.getUserId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getUserEmail()));
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getUserId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getUserEmail()))
                .collect(Collectors.toList());
    }

    public Optional<User> findByUserEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    public boolean registerUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByUserEmail(userRequestDTO.email())) {
            return false;
        }

        PasswordEncoder passwordEncoder = SecurityConfiguration.passwordEncoder();
        User userEntity = new User(
                userRequestDTO.firstName(),
                userRequestDTO.lastName(),
                userRequestDTO.email(),
                passwordEncoder.encode(userRequestDTO.password()));

        userRepository.save(userEntity);
        return true;
    }

    public boolean loginUser(LoginRequestDTO loginRequestDTO) {
        Optional<User> optionalUser = userRepository.findByUserEmail(loginRequestDTO.email());

        if (optionalUser.isPresent()) {
            String storedHashedPassword = optionalUser.get().getPasswordHash();
            return passwordEncoder.matches(loginRequestDTO.password(), storedHashedPassword);
        }

        return false;
    }

    public boolean updateUser(int userId, UserRequestDTO userRequestDTO) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstName(userRequestDTO.firstName());
            user.setLastName(userRequestDTO.lastName());
            user.setUserEmail(userRequestDTO.email());
            user.setPasswordHash(passwordEncoder.encode(userRequestDTO.password()));
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
            return true;
        } else {
            return false;
        }
    }



}