package com.example.kpi.kpimonitoring.service;

import com.example.kpi.dto.KpiRequest;
import com.example.kpi.dto.KpiResponse;
import com.example.kpi.dto.KpiUpdateRequest;
import com.example.kpi.entities.Kpi;
import com.example.kpi.entities.User;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.exceptions.ResourceNotFoundException;
import com.example.kpi.mapper.KpiMapper;
import com.example.kpi.repositories.KPIRepository;
import com.example.kpi.repositories.ReportRepository;
import com.example.kpi.repositories.UserRepository;
import com.example.kpi.security.JwtTokenProvider;
import com.example.kpi.services.KpiService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class KpiServiceTest {

    @InjectMocks
    private KpiService kpiService;

    @Mock
    private KPIRepository kpiRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private KpiMapper kpiMapper;

    @Mock
    private HttpServletRequest httpServletRequest;

    private Kpi kpi;
    private KpiRequest kpiRequest;
    private KpiResponse kpiResponse;

    @BeforeEach
    void setUp() {
        kpi = new Kpi();
        kpi.setId(1L);
        kpi.setName("Test KPI");
        kpi.setValue(100.0);
        kpi.setThreshold(50.0);
        kpi.setIsActive(true);

        LocalDateTime startDate = LocalDateTime.of(2024, 12, 1, 10, 30, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 17, 0, 0);

        kpiRequest = new KpiRequest("Test KPI", 100.0, 50.0, startDate, endDate);
        kpiResponse = new KpiResponse(1L, "Test KPI", 100.0, 50.0, startDate, endDate, true);
    }

    @Test
    void testFindActiveKpis() {
        when(kpiRepository.findByIsActiveTrue()).thenReturn(List.of(kpi));
        when(kpiMapper.mapToKpiResponse(any())).thenReturn(List.of(kpiResponse));

        List<KpiResponse> result = kpiService.findActiveKpis();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Test KPI", result.get(0).name());
    }

    @Test
    void testCreateKpi() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("Authorization", "Bearer testToken");

        when(jwtTokenProvider.getUserIdFromToken("testToken")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        kpiService.createKpi(kpiRequest, mockRequest);

        verify(kpiRepository, times(1)).save(any(Kpi.class));
    }

    @Test
    void testUpdateKpi() {
        KpiUpdateRequest updateRequest = new KpiUpdateRequest(150.0, 75.0);
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));

        kpiService.updateKpi(1L, updateRequest);

        assertEquals(150.0, kpi.getValue());
        assertEquals(75.0, kpi.getThreshold());
    }

    @Test
    void testDeleteKpi() {
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));

        kpiService.deleteById(1L);

        verify(kpiRepository, times(1)).delete(any(Kpi.class));
    }

    @Test
    void testFindByValueRange() throws ResourceExistException {
        when(kpiRepository.findByValueBetween(50.0, 150.0)).thenReturn(List.of(kpi));
        when(kpiMapper.mapToKpiResponse(any())).thenReturn(List.of(kpiResponse));

        List<KpiResponse> result = kpiService.findByValueRange(50.0, 150.0);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Test KPI", result.get(0).name());
    }

    @Test
    void testMakeDeActive() throws ResourceExistException {
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));

        kpiService.makeDeActive(1L);

        assertFalse(kpi.getIsActive());
    }

    @Test
    void testMakeDeActiveWhenAlreadyInactive() {
        kpi.setIsActive(false);
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));

        assertThrows(ResourceExistException.class, () -> kpiService.makeDeActive(1L));
    }

    @Test
    void testGetFilteredKpis() {
        when(kpiRepository.findAll()).thenReturn(List.of(kpi));
        when(kpiMapper.mapToKpiResponse(any())).thenReturn(List.of(kpiResponse));

        List<KpiResponse> result = kpiService.getFilteredKpis(50.0, 150.0, "Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test KPI", result.get(0).name());
    }

    @Test
    void testFindKpiById() {
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));
        when(kpiMapper.toKpiResponse(any(Kpi.class))).thenReturn(kpiResponse);

        KpiResponse result = kpiService.getKpiById(1L);

        assertNotNull(result);
        assertEquals("Test KPI", result.name());
    }

    @Test
    void testFindKpiByIdNotFound() {
        when(kpiRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> kpiService.getKpiById(1L));
    }
}
