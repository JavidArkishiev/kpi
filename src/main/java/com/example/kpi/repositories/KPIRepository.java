package com.example.kpi.repositories;

import com.example.kpi.entities.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Репозиторий для работы с KPI.
 *
 * Предоставляет методы для поиска, удаления и обновления KPI по различным критериям.
 */
public interface KPIRepository extends JpaRepository<Kpi, Long> {

    /**
     * Находит все активные KPI.
     *
     * @return Список активных KPI.
     */
    List<Kpi> findByIsActiveTrue();

    /**
     * Находит KPI, значение которых превышает пороговое значение.
     *
     * @return Список KPI, превышающих порог.
     */
    @Query("SELECT k FROM Kpi k WHERE k.value > k.threshold")
    List<Kpi> findExceedingThreshold();

    /**
     * Находит KPI по названию, начинающемуся с указанного префикса.
     *
     * @param prefix Префикс названия.
     * @return Список KPI с названиями, начинающимися с указанного префикса.
     */
    List<Kpi> findByNameStartingWith(String prefix);

    /**
     * Находит KPI, значение которых находится в указанном диапазоне.
     *
     * @param min Минимальное значение.
     * @param max Максимальное значение.
     * @return Список KPI с значениями в указанном диапазоне.
     */
    List<Kpi> findByValueBetween(Double min, Double max);

    /**
     * Находит KPI по идентификатору пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Список KPI, принадлежащих пользователю.
     */
    @Query("SELECT k FROM Kpi k WHERE k.user.id = :userId")
    List<Kpi> findByUserId(@Param("userId") Long userId);

    /**
     * Обновляет KPI по идентификатору.
     *
     * @param id        Идентификатор KPI.
     * @param value     Новое значение KPI.
     * @param threshold Новый порог KPI.
     */

    /**
     * Удаляет KPI по идентификатору.
     *
     * @param id Идентификатор KPI.
     */
    @Modifying
    void deleteById(Long id); // Название метода соответствует стандарту Spring Data JPA
}
