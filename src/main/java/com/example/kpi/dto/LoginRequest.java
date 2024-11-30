package com.example.kpi.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для входа пользователя в систему.
 */
public record LoginRequest(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String username,

        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
}
