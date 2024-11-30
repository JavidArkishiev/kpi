package com.example.kpi.controllers;

import com.example.kpi.dto.KpiRequest;
import com.example.kpi.dto.KpiResponse;
import com.example.kpi.dto.KpiUpdateRequest;
import com.example.kpi.entities.Kpi;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.services.KpiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления KPI.
 * <p>
 * Предоставляет API для работы с KPI, включая создание, удаление,
 * обновление и фильтрацию данных.
 */
@RestController
@RequestMapping("/api/kpis")
@RequiredArgsConstructor
public class KpiController {

    private final KpiService kpiService;

    /**
     * Возвращает список всех активных KPI.
     *
     * @return Список активных KPI.
     */
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<List<KpiResponse>> getActiveKpis() {
        return ResponseEntity.ok(kpiService.findActiveKpis());
    }

    /**
     * Возвращает список KPI, значение которых превышает пороговое значение.
     *
     * @return Список KPI, превышающих порог.
     */
    @GetMapping("/by-name-prefix/{prefix}")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<List<KpiResponse>> getKpiByNamePrefix(@PathVariable String prefix) {
        return ResponseEntity.ok(kpiService.findByNamePrefix(prefix));
    }

    /**
     * Возвращает список KPI, значение которых находится в указанном диапазоне.
     *
     * @param min Минимальное значение.
     * @param max Максимальное значение.
     * @return Список KPI в указанном диапазоне.
     */

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<List<KpiResponse>> getFilteredKpis(
            @RequestParam(required = false) Double minValue,
            @RequestParam(required = false) Double maxValue,
            @RequestParam(required = false) String namePrefix) {

        List<KpiResponse> filteredKpis = kpiService.getFilteredKpis(minValue, maxValue, namePrefix);
        return ResponseEntity.ok(filteredKpis);
    }

    @GetMapping("user/{userId}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','CEO')")
    public ResponseEntity<List<KpiResponse>> getKpiByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(kpiService.findByUserId(userId));
    }

    @GetMapping("my-using")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<KpiResponse>> getMyKpi(HttpServletRequest request) {
        return ResponseEntity.ok(kpiService.getMyKpi(request));
    }


    @GetMapping()
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','CEO')")
    public ResponseEntity<List<KpiResponse>> getAllKpi() {
        return ResponseEntity.ok(kpiService.getAllKpi());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','CEO')")
    public ResponseEntity<KpiResponse> getKpiById(@PathVariable Long id) {
        return ResponseEntity.ok(kpiService.getKpiById(id));
    }

    /**
     * Создаёт новый KPI.
     *
     * @param kpi Объект KPI для создания.
     * @return Созданный KPI.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<String> createKpi(@RequestBody @Valid KpiRequest kpiRequest,
                                            HttpServletRequest httpServletRequest) {
        kpiService.createKpi(kpiRequest, httpServletRequest);
        return new ResponseEntity<>("KPI created", HttpStatus.CREATED);
    }

    /**
     * Обновляет существующий KPI по идентификатору.
     *
     * @param id        Идентификатор KPI.
     * @param value     Новое значение KPI.
     * @param threshold Новый порог KPI.
     * @return Ответ без содержимого.
     */
    @PutMapping("/{kpiId}")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<Void> updateKpi(@PathVariable Long kpiId, @RequestBody @Valid KpiUpdateRequest request) {
        kpiService.updateKpi(kpiId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Удаляет KPI по идентификатору.
     *
     * @param id Идентификатор KPI.
     * @return Ответ без содержимого.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CEO')")
    public ResponseEntity<Void> deleteKpi(@PathVariable Long id) {
        kpiService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
