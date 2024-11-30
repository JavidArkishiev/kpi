package com.example.kpi.services;

import com.example.kpi.dto.UserRequest;
import com.example.kpi.entities.User;
import com.example.kpi.exceptions.ResourceNotFoundException;
import com.example.kpi.repositories.UserRepository;
import com.example.kpi.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления регистрацией и аутентификацией.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Регистрация нового пользователя.
     *
     * @param userRequest Данные нового пользователя.
     */
    public void register(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует.");
        }

        User user = new User();
        user.setEmail(userRequest.email());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRoles(userRequest.roles());

        userRepository.save(user);
    }

    /**
     * Аутентификация пользователя и генерация токена.
     *
     * @param email    Email пользователя.
     * @param password Пароль пользователя.
     * @return Сгенерированный JWT токен.
     */
    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResourceNotFoundException("Неверный пароль");
        }

        return jwtTokenProvider.generateToken(user);
    }
}
