package org.example.backendclerkio.service;

import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserSuccess() {
        // Inline initialization of user
        User user = new User("Karl", "Bjarnø", "karl@mail.dk", "encodedKarlPassword");

        // Stub the repository method to return the user with ID 1
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Call the service method
        Optional<UserResponseDTO> userResponseDTO = userService.getUser(1);

        // Verify the result
        assertEquals("karl@mail.dk", userResponseDTO.get().email());
    }

    @Test
    void testGetUserNotFound() {
        // Stub the repository method for a non-existent user (ID 99)
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // Call the service method
        Optional<UserResponseDTO> userResponseDTO = userService.getUser(99);

        // Assert that the result is empty
        assertEquals(Optional.empty(), userResponseDTO);
    }
}