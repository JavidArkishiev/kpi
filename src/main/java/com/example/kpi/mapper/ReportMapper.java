package com.example.kpi.mapper;

import com.example.kpi.dto.ReportResponse;
import com.example.kpi.entities.Report;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ReportMapper {

    private final KpiMapper kpiMapper;

    public ReportResponse toReportResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReportDate(),
                report.getUser().getId(),
                kpiMapper.toKpiResponses(report.getKpis()));
    }

    public List<ReportResponse> toReportResponses(List<Report> reports) {
        return reports.stream()
                .map(this::toReportResponse)
                .collect(Collectors.toList());
    }
}
