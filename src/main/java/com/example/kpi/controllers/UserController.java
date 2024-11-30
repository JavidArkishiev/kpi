package com.example.kpi.controllers;

import com.example.kpi.dto.UserRequest;
import com.example.kpi.dto.UserResponse;
import com.example.kpi.entities.User;
import com.example.kpi.enums.Role;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Возвращает список всех пользователей.
     *
     * @return Список пользователей.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Возвращает пользователя по ID.
     *
     * @param id Идентификатор пользователя.
     * @return Пользователь или ошибка 404.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);

    }

    @GetMapping("profile")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','CEO')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUserProfile() {
        return ResponseEntity.ok(userService.getUserProfile());

    }

    /**
     * Создаёт нового пользователя.
     *
     * @param user Данные нового пользователя.
     * @return Созданный пользователь.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<String> createUser(@RequestBody @Valid UserRequest user) throws ResourceExistException {
        userService.createUser(user);
        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param id   Идентификатор пользователя.
     * @param user Новые данные пользователя.
     * @return Обновлённый пользователь или ошибка 404.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<UserRequest> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequest user) {
        return new ResponseEntity<>(userService.updateUser(id, user), HttpStatus.OK);
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id Идентификатор пользователя.
     * @return Ответ без содержимого.
     */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }


}
