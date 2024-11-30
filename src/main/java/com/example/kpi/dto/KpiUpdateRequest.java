package com.example.kpi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record KpiUpdateRequest(
        @NotNull(message = "Значение KPI обязательно.")
        @Positive(message = "Значение KPI должно быть положительным.")
        Double value,
        @NotNull(message = "Пороговое значение KPI обязательно.")
        @Positive(message = "Пороговое значение KPI должно быть положительным.")
        Double threshold
) {
}
