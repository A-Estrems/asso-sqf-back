package fr.estrems.asso_sqf_back.service;

import fr.estrems.asso_sqf_back.dto.UserDto;
import fr.estrems.asso_sqf_back.model.User;
import fr.estrems.asso_sqf_back.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerNewUser_ShouldSaveUser() {
        UserDto dto = new UserDto();
        dto.setUsername("testuser");
        dto.setPassword("password");
        dto.setEmail("testuser@example.com");

        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        User savedUser = new User(1L, dto.getUsername(), "encodedPassword", dto.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User user = userService.registerNewUser(dto);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
    }
}

