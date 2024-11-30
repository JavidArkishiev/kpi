package com.example.kpi.repositories;

import com.example.kpi.entities.Report;

import java.util.Optional;

import com.example.kpi.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с отчётами.
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Находит отчёты, созданные за последние N дней.
     *
     * @param startDate Дата начала поиска.
     * @return Список отчётов.
     */
//    @Query("SELECT r FROM Report r WHERE r.reportDate >= :startDate")
//    List<Report> findRecentReports(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT r FROM Report r WHERE r.reportDate >= :startDate")
    List<Report> findReportsByDateAfter(@Param("startDate") LocalDateTime startDate);

    /**
     * Находит отчёты, созданные пользователями с определённой ролью.
     *
     * @param role Роль пользователя (например, "MANAGER").
     * @return Список отчётов.
     */
    @Query("SELECT r FROM Report r WHERE :role IN (SELECT ur FROM r.user.roles ur)")
    List<Report> findByUserRole(@Param("role") Role role);


    // TODO метод ниже нужен, чтобы Hibernate не делал дополнительные запросы в БД для дозагрузки LAZY-частей (kpi). Так запрос будет один, это снижает нагрузку на БД и решает проблему N+1 запроса.

    /**
     * Находит отчёт вместе с его kpi.
     *
     * @param reportId идентификатор отчёта.
     * @return Отчёт вместе с его kpi.
     */

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.kpis WHERE r.id = :reportId")
    Optional<Report> findReportByIdWithKpis(Long reportId);


    List<Report> findByUserId(Long userId);

    Optional<Report> findByIdAndUserId(Long reportId, Long userId);
}
