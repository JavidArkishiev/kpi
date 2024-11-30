package com.example.kpi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Set;

public record MyReportAndKpis(
        Long id,

        @JsonFormat(pattern = "d, MMMM yyyy hh:mm:ss a")
        LocalDateTime reportDate,

        Long userId,


        Set<KpiResponse> kpiResponseSet
) {
}
