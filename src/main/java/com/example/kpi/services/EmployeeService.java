package com.example.kpi.services;

import com.example.kpi.entities.Kpi;
import com.example.kpi.repositories.KPIRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с Employee.
 */
@Service
@AllArgsConstructor
public class EmployeeService {

    private final KPIRepository kpiRepository;

    /**
     * Сохраняет ежедневный KPI, отправленный сотрудником.
     *
     * @param kpi KPI для сохранения.
     * @return Kpi - Сохранённый KPI.
     */
    public Kpi submitDailyKpi(Kpi kpi) {
        return kpiRepository.save(kpi);
    }
}
