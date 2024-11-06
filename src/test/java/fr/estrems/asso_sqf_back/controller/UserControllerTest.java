package fr.estrems.asso_sqf_back.controller;

import fr.estrems.asso_sqf_back.dto.UserDto;
import fr.estrems.asso_sqf_back.model.User;
import fr.estrems.asso_sqf_back.repository.UserRepository;
import fr.estrems.asso_sqf_back.service.UserService;
import fr.estrems.asso_sqf_back.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // MockBean qui est enregistré dans le contexte Spring

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void registerUser_ShouldReturnOk() throws Exception {
        String requestBody = "{\"username\":\"testuser\",\"password\":\"password\",\"email\":\"testuser@example.com\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void registerUser_PublicAccess_ShouldReturnOk() throws Exception {
        String requestBody = "{\"username\":\"testuser\",\"password\":\"password\",\"email\":\"testuser@example.com\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void updateUser_ShouldUpdateUser() throws Exception {
        // Arrange - Créer un utilisateur existant
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("olduser");
        existingUser.setEmail("old@example.com");

        // Créer le DTO avec les nouvelles valeurs pour la mise à jour
        UserDto updateDto = new UserDto();
        updateDto.setUsername("updateduser");
        updateDto.setEmail("updated@example.com");
        updateDto.setPassword("newpassword");

        // Configurer le comportement des mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser)); // Mock de findById
        when(userService.updateUser(userId, updateDto)).thenReturn(existingUser); // Mock de updateUser

        // Act & Assert - Effectuer la requête PUT pour mettre à jour l'utilisateur
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType("application/json")
                        .content("{ \"username\": \"updateduser\", \"email\": \"updated@example.com\", \"password\": \"newpassword\" }"))
                .andExpect(status().isOk());

        // Vérifier que la méthode de mise à jour a été appelée
        verify(userService, times(1)).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    void deleteUser_ShouldDeleteUser() throws Exception {
        // Arrange
        Long userId = 1L;

        // Simuler l'existence de l'utilisateur et le comportement de suppression
        doNothing().when(userService).deleteUser(userId);

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk());

        // Vérifier que la méthode du service a été appelée
        verify(userService, times(1)).deleteUser(userId);
    }
}

