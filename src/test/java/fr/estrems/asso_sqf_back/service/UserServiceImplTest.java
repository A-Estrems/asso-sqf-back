package fr.estrems.asso_sqf_back.service;

import fr.estrems.asso_sqf_back.dto.UserDto;
import fr.estrems.asso_sqf_back.model.User;
import fr.estrems.asso_sqf_back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerNewUser_ShouldSaveUser() {
        UserDto dto = new UserDto();
        dto.setUsername("newuser");
        dto.setPassword("password");
        dto.setEmail("newuser@example.com");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword("encodedPassword");
        user.setEmail(dto.getEmail());

        // Mock du comportement du repository
        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Appel du service
        User savedUser = userService.registerNewUser(dto);

        // Assertions
        assertNotNull(savedUser);
        assertEquals("newuser", savedUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerNewUser_UsernameTaken_ShouldThrowException() {
        UserDto dto = new UserDto();
        dto.setUsername("existinguser");
        dto.setPassword("password");
        dto.setEmail("existinguser@example.com");

        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(IllegalStateException.class, () -> userService.registerNewUser(dto));
    }
}

