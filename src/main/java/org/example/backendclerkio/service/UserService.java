package org.example.backendclerkio.service;

import org.example.backendclerkio.config.SecurityConfiguration;
import org.example.backendclerkio.dto.LoginRequestDTO;
import org.example.backendclerkio.dto.UserRequestDTO;
import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
                        user.getUserEmail(),
                        user.isAdmin()));
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getUserId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getUserEmail(),
                        user.isAdmin()))
                .collect(Collectors.toList());
    }

    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByUserEmail(userRequestDTO.email())) {
            throw new RuntimeException("User with this email already exists.");
        }

        PasswordEncoder passwordEncoder = SecurityConfiguration.passwordEncoder();
        User userEntity = new User(
                userRequestDTO.firstName(),
                userRequestDTO.lastName(),
                userRequestDTO.email(),
                passwordEncoder.encode(userRequestDTO.password())
        );

        User savedUser = userRepository.save(userEntity);

        return new UserResponseDTO(
                savedUser.getUserId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getUserEmail(),
                savedUser.isAdmin()
        );
    }


    public Optional<UserResponseDTO> updateUser(int userId, UserRequestDTO userRequestDTO) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        if (userRequestDTO.password() != null) {
            user.setPasswordHash(passwordEncoder.encode(userRequestDTO.password()));
        } else {
            user.setFirstName(userRequestDTO.firstName());
            user.setLastName(userRequestDTO.lastName());
            user.setUserEmail(userRequestDTO.email());
        }

        User updatedUser = userRepository.save(user);

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                updatedUser.getUserId(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getUserEmail(),
                updatedUser.isAdmin()
        );

        return Optional.of(userResponseDTO);
    }

    public void makeUserAdmin(String userMail) {
        Optional<User> optionalUser = userRepository.findByUserEmail(userMail);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setAdmin(true);;
            userRepository.save(user);
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

    public Optional<User> findByUserEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByUserEmail(email);
    }

    public boolean userExistsByUserId(int userId) {
        return userRepository.existsByUserId(userId);
    }

    public Optional<User> findUserById(int userId){
        return userRepository.findUserByUserId(userId);
    }

    public UserResponseDTO getUserResponseDTOFromPrincipal(Principal principal) {
        Optional<User> optionalUser = userRepository.findByUserEmail(principal.getName());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        } else {
            User user = optionalUser.get();
            return new UserResponseDTO(
                    user.getUserId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getUserEmail(),
                    user.isAdmin());
        }
    }



}