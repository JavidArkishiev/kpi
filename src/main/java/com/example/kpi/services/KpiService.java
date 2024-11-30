package com.example.kpi.services;

import com.example.kpi.dto.KpiRequest;
import com.example.kpi.dto.KpiResponse;
import com.example.kpi.dto.KpiUpdateRequest;
import com.example.kpi.entities.Kpi;
import com.example.kpi.entities.Report;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.exceptions.ResourceNotFoundException;
import com.example.kpi.mapper.KpiMapper;
import com.example.kpi.repositories.KPIRepository;
import com.example.kpi.repositories.ReportRepository;
import com.example.kpi.repositories.UserRepository;
import com.example.kpi.security.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с KPI.
 * <p>
 * Предоставляет методы для операций с KPI, таких как получение активных KPI,
 * фильтрация по значениям, обновление, удаление и создание новых KPI.
 */
@Service
@RequiredArgsConstructor
public class KpiService {

    private final KPIRepository kpiRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ReportRepository reportRepository;
    private final KpiMapper kpiMapper;
    private final EntityManager entityManager;


    /**
     * Возвращает список всех активных KPI.
     *
     * @return Список активных KPI.
     */
    public List<KpiResponse> findActiveKpis() {
        var kpiList = kpiRepository.findByIsActiveTrue();

        if (kpiList.isEmpty()) {
            return Collections.emptyList();
        }
        return kpiMapper.mapToKpiResponse(kpiList);
    }

    /**
     * Возвращает список KPI, значение которых превышает пороговое значение.
     *
     * @return Список KPI, превышающих порог.
     */
    public List<Kpi> findExceedingThreshold() {
        return kpiRepository.findExceedingThreshold();
    }

    /**
     * Возвращает список KPI, название которых начинается с указанного префикса.
     *
     * @param prefix Префикс названия KPI.
     * @return Список KPI, соответствующих префиксу.
     */
    public List<KpiResponse> findByNamePrefix(String prefix) {
        var kpiList = kpiRepository.findByNameStartingWith(prefix);
        if (kpiList.isEmpty()) {
            return Collections.emptyList();
        }
        return kpiMapper.mapToKpiResponse(kpiList);

    }

    /**
     * Возвращает список KPI, значение которых находится в указанном диапазоне.
     *
     * @param min Минимальное значение KPI.
     * @param max Максимальное значение KPI.
     * @return Список KPI в указанном диапазоне.
     */
    public List<KpiResponse> findByValueRange(Double min, Double max) throws ResourceExistException {
        if (max < min) {
            throw new ResourceExistException("Max value is not little than min value");
        }
        var kpiList = kpiRepository.findByValueBetween(min, max);
        if (kpiList.isEmpty()) {
            return Collections.emptyList();
        }
        return kpiMapper.mapToKpiResponse(kpiList);
    }

    /**
     * Возвращает список KPI, принадлежащих указанному пользователю.
     *
     * @param userId Идентификатор пользователя.
     * @return Список KPI пользователя.
     */
    public List<KpiResponse> findByUserId(Long userId) {
        List<Report> reportList = reportRepository.findByUserId(userId);
        return getKpiResponses(reportList);
    }

    /**
     * Обновляет существующий KPI по идентификатору.
     *
     * @param id        Идентификатор KPI.
     * @param value     Новое значение KPI.
     * @param threshold Новый порог KPI.
     */
    @Transactional
    public void updateKpi(Long id, KpiUpdateRequest request) {
        var oldKpi = kpiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));
        oldKpi.setValue(request.value());
        oldKpi.setThreshold(request.threshold());
        kpiRepository.save(oldKpi);
    }

    /**
     * Удаляет KPI по идентификатору.
     *
     * @param id Идентификатор KPI.
     */
    @Transactional
    public void deleteById(Long id) {
        kpiRepository.findById(id)
                .ifPresentOrElse(
                        kpiRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Kpi not found with : " + id);
                        }
                );
    }

    /**
     * Создаёт новый KPI.
     *
     * @param kpi Объект KPI для сохранения.
     * @return Сохранённый KPI.
     */
    public void createKpi(KpiRequest kpiRequest, HttpServletRequest servletRequest) {
        String authHeader = servletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            var userId = jwtTokenProvider.getUserIdFromToken(token);

            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Kpi kpi = new Kpi();
            kpi.setName(kpiRequest.name());
            kpi.setStartDate(kpiRequest.startDate());
            kpi.setThreshold(kpiRequest.threshold());
            kpi.setValue(kpiRequest.value());
            kpi.setIsActive(true);
            kpi.setEndDate(kpiRequest.endDate());
            kpi.setUser(user);
            kpiRepository.save(kpi);

        }
    }

    public List<KpiResponse> getAllKpi() {
        var kpiList = kpiRepository.findAll();
        if (kpiList.isEmpty()) {
            return Collections.emptyList();
        }
        return kpiMapper.mapToKpiResponse(kpiList);

    }

    public List<KpiResponse> getMyKpi(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        var userId = jwtTokenProvider.getUserIdFromToken(token);
        var reportList = reportRepository.findByUserId(userId);
        return getKpiResponses(reportList);
    }

    private List<KpiResponse> getKpiResponses(List<Report> reportList) {
        if (reportList.isEmpty()) {
            return Collections.emptyList();
        }
        return reportList.stream()
                .flatMap(report -> report.getKpis().stream())
                .distinct()
                .map(kpi -> new KpiResponse(
                        kpi.getId(),
                        kpi.getName(),
                        kpi.getValue(),
                        kpi.getThreshold(),
                        kpi.getStartDate(),
                        kpi.getEndDate(),
                        kpi.getIsActive()
                ))
                .collect(Collectors.toList());
    }

    public KpiResponse getKpiById(Long kpiId) {
        var kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));
        return kpiMapper.toKpiResponse(kpi);

    }

    public List<KpiResponse> getFilteredKpis(Double minValue, Double maxValue, String namePrefix) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Kpi> query = cb.createQuery(Kpi.class);
        Root<Kpi> root = query.from(Kpi.class);

        List<Predicate> predicates = new ArrayList<>();

        if (minValue != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("value"), minValue));
        }

        if (maxValue != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("value"), maxValue));
        }

        if (namePrefix != null) {
            predicates.add(cb.like(root.get("name"), namePrefix + "%"));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Kpi> result = entityManager.createQuery(query).getResultList();
        return result.stream()
                .map(kpiMapper::toKpiResponse)
                .collect(Collectors.toList());
    }

    public void makeDeActive(Long kpiId) throws ResourceExistException {
        var kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI can not found"));
        if (!kpi.getIsActive()) {
            throw new ResourceExistException("This KPI already deActivated");
        }
        kpi.setIsActive(false);
        kpiRepository.save(kpi);
    }

    public void makeActive(Long kpiId) throws ResourceExistException {
        var kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI can not found"));
        if (kpi.getIsActive()) {
            throw new ResourceExistException("This KPI already activated");
        }
        kpi.setIsActive(true);
        kpiRepository.save(kpi);
    }
}
