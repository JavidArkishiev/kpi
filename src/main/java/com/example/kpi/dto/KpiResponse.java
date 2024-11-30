package com.example.kpi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO с информацией о KPI.
 */
// TODO используйте эту DTO для возврата из сервиса и контроллера
public record KpiResponse(Long id,
                          String name,
                          Double value,
                          Double threshold,
                          @JsonFormat(pattern = "d, MMMM yyyy hh:mm:ss a")
                          LocalDateTime startDate,
                          @JsonFormat(pattern = "d, MMMM yyyy hh:mm:ss a")
                          LocalDateTime endDate,
                          Boolean isActive) {

}