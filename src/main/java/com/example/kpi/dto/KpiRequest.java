package com.example.kpi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * DTO для создания или обновления KPI.
 * <p>
 * Используется для передачи данных, необходимых для создания или обновления KPI.
 * Содержит:
 * - Название KPI.
 * - Значение KPI.
 * - Пороговое значение KPI.
 * - Дата начала действия KPI.
 * - Дата окончания действия KPI.
 * - Флаг активности KPI.
 */
public record KpiRequest(

        /**
         * Название KPI.
         * Обязательное поле. Не может быть пустым.
         */
        @NotBlank(message = "Название KPI не может быть пустым.")
        String name,

        /**
         * Значение KPI.
         * Обязательное поле. Должно быть положительным числом.
         */
        @NotNull(message = "Значение KPI обязательно.")
        @Positive(message = "Значение KPI должно быть положительным.")
        Double value,

        /**
         * Пороговое значение KPI.
         * Обязательное поле. Должно быть положительным числом.
         */
        @NotNull(message = "Пороговое значение KPI обязательно.")
        @Positive(message = "Пороговое значение KPI должно быть положительным.")
        Double threshold,

        /**
         * Дата начала действия KPI.
         * Обязательное поле.
         */
        @NotNull(message = "Дата начала действия KPI обязательна.")
        LocalDateTime startDate,

        /**
         * Дата окончания действия KPI.
         * Может быть null, если действие не ограничено.
         */
        LocalDateTime endDate
) {
}