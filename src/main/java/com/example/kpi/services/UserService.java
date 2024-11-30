package com.example.kpi.services;

import com.example.kpi.dto.UserRequest;
import com.example.kpi.dto.UserResponse;
import com.example.kpi.entities.User;
import com.example.kpi.enums.Role;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.exceptions.ResourceNotFoundException;
import com.example.kpi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    /**
     * Возвращает всех пользователей.
     *
     * @return Список пользователей.
     */
    public List<UserResponse> getAllUsers() {
        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userList) {
            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getRoles().toString()
            );
            userResponseList.add(userResponse);
        }
        return userResponseList;
    }

    /**
     * Возвращает пользователя по ID.
     *
     * @param id Идентификатор пользователя.
     * @return Найденный пользователь или пустой Optional.
     */
    public UserResponse getUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRoles().toString()
        );
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param user Пользователь для создания.
     * @return Созданный пользователь.
     */
    public void createUser(UserRequest user) throws ResourceExistException {
        if (userRepository.existsByEmail(user.email())) {
            throw new ResourceExistException("User already have");
        }
        User createdUser = new User();
        createdUser.setEmail(user.email());
        createdUser.setPassword(user.password());
        createdUser.setRoles(user.roles());
        userRepository.save(createdUser);
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param id   Идентификатор пользователя.
     * @param user Обновлённые данные пользователя.
     * @return Обновлённый пользователь или пустой Optional, если не найден.
     */
    public UserRequest updateUser(Long id, UserRequest user) {
        userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(user.email());
                    existingUser.setPassword(passwordEncoder.encode(user.password()));
                    existingUser.setRoles(user.roles());
                    return userRepository.save(existingUser);
                });
        return user;
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id Идентификатор пользователя.
     */
    public void deleteUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this id " + id));
        userRepository.delete(user);
    }

    /**
     * Добавляет роль пользователю.
     *
     * @param userId ID пользователя.
     * @param role   Роль для добавления.
     * @return Обновлённый пользователь.
     */
    public UserResponse addRoleToUser(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.addRole(role);
        userRepository.save(user);
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRoles().toString()
        );
    }

    /**
     * Удаляет роль у пользователя.
     *
     * @param userId ID пользователя.
     * @param role   Роль для удаления.
     * @return Обновлённый пользователь.
     */
    public UserResponse removeRoleFromUser(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.removeRole(role);
        userRepository.save(user);
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRoles().toString()
        );
    }

    /**
     * Возвращает пользователей с указанной ролью.
     *
     * @param role Роль для фильтрации.
     * @return Список пользователей с данной ролью.
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findAllByRoles(role);
    }

    public UserResponse getUserProfile() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRoles().toString()
        );
    }
}
