package com.example.kpi.dto;

import com.example.kpi.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * DTO для создания или обновления пользователя.
 *
 * Используется для передачи данных, необходимых для создания нового пользователя или обновления существующего.
 * Содержит:
 * - Электронную почту пользователя (логин).
 * - Пароль.
 * - Список ролей пользователя.
 */
public record UserRequest(

        /**
         * Электронная почта пользователя, выступает в качестве логина.
         * Уникальное обязательное поле.
         */
        @NotNull(message = "Email пользователя обязателен.")
        @Email(message = "Некорректный формат электронной почты.")
        @Size(max = 100, message = "Email не должен превышать 100 символов.")
        String email,

        /**
         * Пароль пользователя.
         * Обязательное поле, минимальная длина — 8 символов.
         */
        @NotNull(message = "Пароль пользователя обязателен.")
        @Size(min = 8, message = "Пароль должен содержать минимум 8 символов.")
        String password,

        /**
         * Роли пользователя.
         * Обязательное поле, должно содержать хотя бы одну роль.
         */
        @NotNull(message = "Роли пользователя обязательны.")
        Set<Role> roles
) {
}
