package com.example.kpi.services;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private KPIRepository kpiRepository;

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private KpiMapper kpiMapper;

    @Mock
    private HttpServletRequest servletRequest;

    private User mockUser;
    private Kpi mockKpi;
    private Report mockReport;

    @BeforeEach
    public void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRoles(Collections.singleton(Role.EMPLOYEE));

        mockKpi = new Kpi();
        mockKpi.setId(1L);
        mockKpi.setName("Test KPI");

        mockReport = new Report();
        mockReport.setId(1L);
        mockReport.setReportDate(LocalDateTime.now());
        mockReport.setUser(mockUser);
        mockReport.getKpis().add(mockKpi);
    }

    @Test
    public void testCreateReport() {
        when(jwtTokenProvider.getUserIdFromToken(anyString())).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(kpiRepository.findById(anyLong())).thenReturn(Optional.of(mockKpi));
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        reportService.createReport(1L, servletRequest);

        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    public void testAddKpiToReport() throws ResourceExistException {
        when(jwtTokenProvider.getUserIdFromToken(anyString())).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(reportRepository.findById(anyLong())).thenReturn(Optional.of(mockReport));
        when(kpiRepository.findById(anyLong())).thenReturn(Optional.of(mockKpi));
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        ReportResponse response = reportService.addKpiToReport(1L, 1L, servletRequest);

        assertNotNull(response);
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    public void testRemoveKpiFromReport() {
        when(reportRepository.findReportByIdWithKpis(anyLong())).thenReturn(Optional.of(mockReport));
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        ReportResponse response = reportService.removeKpiFromReport(1L, 1L);

        assertNotNull(response);
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    public void testGetReportById() {
        when(reportRepository.findById(anyLong())).thenReturn(Optional.of(mockReport));

        ReportResponse response = reportService.getReportById(1L);

        assertNotNull(response);
        assertEquals(mockReport.getId(), response.id());
    }

    @Test
    public void testDeleteReport() {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer mockToken");
        when(jwtTokenProvider.getUserIdFromToken(anyString())).thenReturn(1L);
        when(reportRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(mockReport));

        reportService.deleteReport(1L, servletRequest);

        verify(reportRepository, times(1)).delete(any(Report.class));
    }

    @Test
    public void testGetReportsByUserRole() {
        when(reportRepository.findByUserRole(any(Role.class))).thenReturn(List.of(mockReport));

        List<ReportResponse> responses = reportService.getReportsByUserRole(Role.EMPLOYEE);

        assertFalse(responses.isEmpty());
    }

    @Test
    public void testGetMyReport() {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer mockToken");
        when(jwtTokenProvider.getUserIdFromToken(anyString())).thenReturn(1L);
        when(reportRepository.findByUserId(anyLong())).thenReturn(List.of(mockReport));

        List<MyReportAndKpis> responses = reportService.getMyReport(servletRequest);

        assertFalse(responses.isEmpty());
    }

    @Test
    public void testCreateReport_UserNotFound() {
        when(jwtTokenProvider.getUserIdFromToken(anyString())).thenReturn(2L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reportService.createReport(1L, servletRequest));
    }

    @Test
    public void testAddKpiToReport_UserNotAuthorized() {
        when(jwtTokenProvider.getUserIdFromToken(anyString())).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(reportRepository.findById(anyLong())).thenReturn(Optional.of(mockReport));
        when(kpiRepository.findById(anyLong())).thenReturn(Optional.of(mockKpi));

        ResourceExistException exception = assertThrows(ResourceExistException.class, () ->
                reportService.addKpiToReport(1L, 1L, servletRequest));

        assertEquals("You are not allowed to modify this report. Because this is not your report", exception.getMessage());
    }

}
