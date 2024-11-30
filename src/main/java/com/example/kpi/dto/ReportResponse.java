package com.example.kpi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO для ответа с данными отчёта.
 * <p>
 * Используется для передачи клиенту данных об отчёте.
 * Содержит:
 * - ID отчёта.
 * - Дата создания отчёта.
 * - Идентификатор пользователя, создавшего отчёт.
 * - Список идентификаторов связанных KPI.
 */
public record ReportResponse(

        /**
         * Уникальный идентификатор отчёта.
         * Обязательное поле.
         */
        Long id,

        /**
         * Дата создания отчёта.
         * Обязательное поле.
         */
        @JsonFormat(pattern = "d, MMMM yyyy hh:mm:ss a")
        LocalDateTime reportDate,

        /**
         * Идентификатор пользователя, создавшего отчёт.
         * Обязательное поле.
         */
        Long userId,

        /**
         * Список идентификаторов KPI, связанных с отчётом.
         * Обязательное поле.
         */
        Set<KpiResponse> kpiResponseSet
) {
}
