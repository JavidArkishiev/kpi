//package com.example.kpi.dto;
//
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.NotNull;
//import java.time.LocalDateTime;
//import java.util.Set;
//
///**
// * DTO для запроса на создание отчёта.
// *
// * Используется для передачи данных, необходимых для создания нового отчёта.
// * Содержит:
// * - Дату создания отчёта.
// * - Идентификатор пользователя, создавшего отчёт.
// * - Список идентификаторов связанных KPI.
// */
//public record ReportRequest(
//
//        /**
//         * Дата создания отчёта.
//         * Обязательное поле.
//         */
//        @NotNull(message = "Дата создания отчёта обязательна.")
//        LocalDateTime reportDate
//
//        /**
//         * Идентификатор пользователя, создавшего отчёт.
//         * Обязательное поле.
//         */
//
//        /**
//         * Список идентификаторов связанных KPI.
//         * Минимум один KPI обязателен.
//         */
//) {
//}
