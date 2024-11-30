package com.example.kpi.mapper;

import com.example.kpi.dto.KpiResponse;
import com.example.kpi.entities.Kpi;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class KpiMapper {

    public KpiResponse toKpiResponse(Kpi kpi) {
        return new KpiResponse(kpi.getId(),
                kpi.getName(),
                kpi.getValue(),
                kpi.getThreshold(),
                kpi.getEndDate(),
                kpi.getStartDate(),
                kpi.getIsActive());
    }

    public Set<KpiResponse> toKpiResponses(Set<Kpi> kpis) {
        return kpis.stream()
                .map(this::toKpiResponse)
                .collect(Collectors.toSet());
    }

    public List<KpiResponse> mapToKpiResponse(List<Kpi> kpis) {
        return kpis.stream()
                .map(this::toKpiResponse)
                .collect(Collectors.toList());
    }

    public Set<Long> toReportResponseIds(Set<Kpi> kpis) {
        return kpis.stream()
                .map(Kpi::getId)
                .collect(Collectors.toSet());
    }

}
