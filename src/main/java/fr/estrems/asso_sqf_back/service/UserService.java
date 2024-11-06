package fr.estrems.asso_sqf_back.service;

import fr.estrems.asso_sqf_back.dto.UserDto;
import fr.estrems.asso_sqf_back.model.User;

import java.util.List;

public interface UserService {
    User registerNewUser(UserDto userDto);
    User findByUsername(String username);
    List<User> findAllUsers();
    User updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
}
