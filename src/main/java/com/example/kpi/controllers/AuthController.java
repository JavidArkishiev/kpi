package com.example.kpi.controllers;

import com.example.kpi.dto.JwtResponse;
import com.example.kpi.dto.LoginRequest;
import com.example.kpi.dto.UserRequest;
import com.example.kpi.entities.User;
import com.example.kpi.enums.Role;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.services.AuthService;
import com.example.kpi.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления авторизацией и аутентификацией.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Регистрация нового пользователя.
     *
     * @param userRequest Данные нового пользователя.
     * @return Успешное сообщение.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRequest userRequest) throws ResourceExistException {
        authService.register(userRequest);
        return ResponseEntity.ok("Регистрация прошла успешно!");
    }

    /**
     * Вход пользователя в систему.
     *
     * @param loginRequest Данные для входа.
     * @return JWT токен.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.authenticate(loginRequest.username(), loginRequest.password());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    /**
     * Добавление роли пользователю.
     *
     * @param userId ID пользователя.
     * @param role   Роль для добавления.
     * @return Обновлённый пользователь.
     */

}
