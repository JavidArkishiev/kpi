package com.example.kpi.controllers;

import com.example.kpi.dto.MyReportAndKpis;
import com.example.kpi.dto.ReportResponse;
import com.example.kpi.enums.Role;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.services.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер для управления отчётами.
 * <p>
 * Предоставляет API для создания, получения, удаления и фильтрации отчётов.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Возвращает список отчётов, созданных за последние N дней.
     *
     * @param days Количество последних дней для фильтрации.
     * @return Список отчётов.
     */

    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<List<ReportResponse>> getRecentReports(@RequestParam("days") int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        List<ReportResponse> recentReports = reportService.getRecentReports(startDate);
        return ResponseEntity.ok(recentReports);
    }

    /**
     * Возвращает список отчётов, созданных пользователями с определённой ролью.
     *
     * @param role Роль пользователя.
     * @return Список отчётов.
     */
    @GetMapping("/by-role")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<List<ReportResponse>> getReportsByUserRole(@RequestParam Role role) {
        List<ReportResponse> reportsByRole = reportService.getReportsByUserRole(role);
        return ResponseEntity.ok(reportsByRole);
    }


    /**
     * Создаёт новый отчёт.
     *
     * @param reportRequest Данные для создания отчёта.
     * @return Созданный отчёт.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<String> createReport(@RequestParam Long kpiId,
                                               HttpServletRequest request) {
        reportService.createReport(kpiId, request);
        return new ResponseEntity<>("Report created", HttpStatus.CREATED);
    }

    @GetMapping("my-report")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MyReportAndKpis>> getMyReport(HttpServletRequest request) {
        return ResponseEntity.ok(reportService.getMyReport(request));
    }


    /**
     * Удаляет отчёт по ID.
     *
     * @param reportId Идентификатор отчёта.
     * @return Ответ без содержимого.
     */
    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId,
                                             HttpServletRequest servletRequest) {
        reportService.deleteReport(reportId, servletRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает отчёт по ID.
     *
     * @param id Идентификатор отчёта.
     * @return Отчёт.
     */
    @GetMapping("/{reportId}")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long reportId) {
        return new ResponseEntity<>(reportService.getReportById(reportId), HttpStatus.OK);

    }

    /**
     * Добавляет KPI к отчёту.
     *
     * @param reportId Идентификатор отчёта.
     * @param kpiId    Идентификатор KPI.
     * @return Обновлённый отчёт.
     */
    @PutMapping("/add-kpi")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<ReportResponse> addKpiToReport(@RequestParam Long reportId,
                                                         @RequestParam Long kpiId,
                                                         HttpServletRequest request) throws ResourceExistException {
        ReportResponse response = reportService.addKpiToReport(reportId, kpiId, request);
        return ResponseEntity.ok(response);
    }


    /**
     * Удаляет KPI из отчёта.
     *
     * @param reportId Идентификатор отчёта.
     * @param kpiId    Идентификатор KPI.
     * @return Обновлённый отчёт.
     */
    @PutMapping("/remove-kpi")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<ReportResponse> removeKpiFromReport(@RequestParam Long reportId,
                                                              @RequestParam Long kpiId) {
        ReportResponse response = reportService.removeKpiFromReport(reportId, kpiId);
        return ResponseEntity.ok(response);
    }
}
