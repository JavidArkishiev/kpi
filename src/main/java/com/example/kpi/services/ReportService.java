package com.example.kpi.services;

import com.example.kpi.dto.KpiResponse;
import com.example.kpi.dto.MyReportAndKpis;
import com.example.kpi.dto.ReportResponse;
import com.example.kpi.entities.Kpi;
import com.example.kpi.entities.Report;
import com.example.kpi.entities.User;
import com.example.kpi.enums.Role;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.exceptions.ResourceNotFoundException;
import com.example.kpi.mapper.KpiMapper;
import com.example.kpi.mapper.ReportMapper;
import com.example.kpi.repositories.KPIRepository;
import com.example.kpi.repositories.ReportRepository;
import com.example.kpi.repositories.UserRepository;
import com.example.kpi.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для работы с отчётами.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final KPIRepository kpiRepository;
    private final ReportMapper reportMapper;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KpiMapper kpiMapper;

    /**
     * Создаёт новый отчёт.
     *
     * @param reportRequest Данные для создания отчёта.
     * @param kpiId
     * @return Созданный отчёт.
     */
    public void createReport(Long kpiId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        var userId = jwtTokenProvider.getUserIdFromToken(token);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Kpi kpis = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));

        Report report = new Report();
        report.setReportDate(LocalDateTime.now());
        report.setUser(user);
        report.getKpis().add(kpis);
        kpis.getReports().add(report);
        reportRepository.save(report);
    }


    // TODO такое ощущение, что метод был сначала add, а потом Вы его переделали на remove. Ваш вариант закомментирован. Ниже написал оба метода правильно

    /**
     * Удаляет KPI из отчёта.
     *
     * @param reportId ID отчёта.
     * @param kpiId    ID KPI.
     */
    public ReportResponse removeKpiFromReport(Long reportId, Long kpiId) {
        Report report = reportRepository.findReportByIdWithKpis(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with this reportId: " + reportId));
        Kpi reportKpi = report.getKpis().stream()
                .filter(kpi -> kpi.getId().equals(kpiId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Kpi not found with this kipsId: " + kpiId)); // TODO привыкайте писать сообщения в таком формате
        report.removeKpi(reportKpi);
        report = reportRepository.save(report);
        return reportMapper.toReportResponse(report);
    }


    /**
     * Добавляет KPI в отчёт по идентификатору KPI (KPI должен существовать в БД).
     *
     * @param reportId ID отчёта.
     * @param kpiId    ID KPI.
     */
    public ReportResponse addKpiToReport(Long reportId, Long kpiId, HttpServletRequest request) throws ResourceExistException {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        var userId = jwtTokenProvider.getUserIdFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        if (!report.getUser().equals(user)) {
            throw new ResourceExistException("You are not allowed to modify this report. " +
                    "Because this is not your report");
        }

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));

        if (report.getKpis().contains(kpi)) {
            throw new ResourceExistException("This KPI is already added to your report.");
        }

        report.getKpis().add(kpi);
        reportRepository.save(report);
        return reportMapper.toReportResponse(report);
    }


    /**
     * Возвращает отчёт по ID.
     *
     * @param id Идентификатор отчёта.
     * @return Найденный отчёт или пустой Optional.
     */
    public ReportResponse getReportById(Long id) {
        var report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        Set<KpiResponse> kpiResponses = report.getKpis().stream()
                .map(kpi -> new KpiResponse(
                        kpi.getId(),
                        kpi.getName(),
                        kpi.getValue(),
                        kpi.getThreshold(),
                        kpi.getStartDate(),
                        kpi.getEndDate(),
                        kpi.getIsActive()
                ))
                .collect(Collectors.toSet());

        return new ReportResponse(
                report.getId(),
                report.getReportDate(),
                report.getUser().getId(),
                kpiResponses
        );
    }


    /**
     * Удаляет отчёт по ID.
     *
     * @param reportId Идентификатор отчёта.
     */
    public void deleteReport(Long reportId, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        var userId = jwtTokenProvider.getUserIdFromToken(token);
        var report = reportRepository.findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Can not your Report"));
        reportRepository.delete(report);
    }

    /**
     * Возвращает список отчётов, созданных за последние N дней.
     *
     * @param startDate Дата начала поиска.
     * @return Список отчётов.
     */
    public List<ReportResponse> getRecentReports(LocalDateTime startDate) {
        List<Report> reports = reportRepository.findReportsByDateAfter(startDate);

        return reportMapper.toReportResponses(reports);
    }

    public List<ReportResponse> getReportsByUserRole(Role roleName) {
        List<Report> reports = reportRepository.findByUserRole(roleName);

        return reportMapper.toReportResponses(reports);
    }


    public List<MyReportAndKpis> getMyReport(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        long userId = jwtTokenProvider.getUserIdFromToken(token);
        var reportList = reportRepository.findByUserId(userId);
        return reportList.stream()
                .map(report -> new MyReportAndKpis(
                        report.getId(),
                        report.getReportDate(),
                        report.getUser().getId(),
                        kpiMapper.toKpiResponses(report.getKpis())
                ))
                .collect(Collectors.toList());
    }
}
