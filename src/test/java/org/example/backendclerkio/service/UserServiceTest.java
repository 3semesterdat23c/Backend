package org.example.backendclerkio.service;

import org.example.backendclerkio.dto.UserRequestDTO;
import org.example.backendclerkio.dto.UserResponseDTO;
import org.example.backendclerkio.entity.User;
import org.example.backendclerkio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        System.out.println("Testing: testGetUserSuccess...");
        User user = new User("Karl", "Bjarnø", "karl@mail.dk", "encodedKarlPassword");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        String expectedEmail = "karl@mail.dk";

        Optional<UserResponseDTO> userResponseDTO = userService.getUser(1);
        String actualEmail = userResponseDTO.get().email();

        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    void testGetUserNotFound() {
        System.out.println("Testing: testGetUserNotFound...");
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        Optional<UserResponseDTO> userResponseDTO = userService.getUser(99);

        assertEquals(Optional.empty(), userResponseDTO);
    }

    @Test
    void testGetAllUsers() {
        System.out.println("Testing: testGetAllUsers...");
        User user1 = new User("Karl", "Bjarnø", "karl@mail.dk", "encodedKarlPassword");
        User user2 = new User("Anders", "Ludvigsen", "anders@mail.dk", "encodedAndersPassword");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        int expectedSize = 2;
        String expectedEmailOfUser1 = "karl@mail.dk";
        String expectedEmailOfUser2 = "anders@mail.dk";

        List<UserResponseDTO> actualUserList = userService.getAllUsers();

        int actualSize = actualUserList.size();
        String actualEmailOfUser1 = actualUserList.get(0).email();
        String actualEmailOfUser2 = actualUserList.get(1).email();

        assertEquals(expectedSize, actualSize);
        assertEquals(expectedEmailOfUser1, actualEmailOfUser1);
        assertEquals(expectedEmailOfUser2, actualEmailOfUser2);
    }

    @Test
    void testCreateUser() {
        System.out.println("Testing: testCreateUser...");
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Aleksander",
                "Gregersen",
                "aleksander@mail.dk",
                "aleksanderPassword");
        when(userRepository.existsByUserEmail(userRequestDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(userRequestDTO.password())).thenReturn("encodedAleksanderPassword");
        User savedUser = new User(
                userRequestDTO.firstName(),
                userRequestDTO.lastName(),
                userRequestDTO.email(),
                "encodedPassword" // this is the mocked encoded password
        );
        savedUser.setUserId(1);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        UserResponseDTO userResponseDTO = userService.registerUser(userRequestDTO);

        verify(userRepository, times(1)).save(Mockito.any(User.class));
        assertNotNull(userResponseDTO);
        assertEquals(userRequestDTO.email(), userResponseDTO.email());
        assertEquals(userRequestDTO.firstName(), userResponseDTO.firstName());
        assertEquals(userRequestDTO.lastName(), userResponseDTO.lastName());
        assertFalse(userResponseDTO.isAdmin());
    }
}