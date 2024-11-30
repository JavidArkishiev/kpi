package com.example.kpi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO для возврата данных об ошибке.
 * <p>
 * Используется для стандартизации структуры ответа об ошибке,
 * чтобы клиент получал унифицированную информацию о проблемах.
 * <p>
 * Содержит:
 * - Временную метку (`timestamp`) — время возникновения ошибки.
 * - HTTP-статус ошибки (`status`).
 * - Краткое описание ошибки (`error`).
 * - Детальное сообщение об ошибке (`message`).
 */
public record ErrorResponse(
        /**
         * Временная метка возникновения ошибки.
         */
        @JsonFormat(pattern = "d, MMMM yyyy hh:mm:ss a")
        LocalDateTime timestamp,

        /**
         * HTTP-статус ошибки (например, 404 для "Not Found").
         */
        int status,

        /**
         * Краткое описание ошибки.
         */
        /**
         * Детальное сообщение об ошибке.
         */
        String message
) {
}
