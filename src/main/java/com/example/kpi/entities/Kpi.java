package com.example.kpi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс Kpi представляет ключевые показатели эффективности (KPI).
 */
@Getter
@Setter
@ToString(exclude = {"reports", "user"})
@EqualsAndHashCode(exclude = {"reports", "user"})
@Entity
@Table(name = "kpis")
public class Kpi {

    /**
     * Уникальный идентификатор KPI.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название KPI.
     * Пример: "Производительность", "Точность".
     */
    @Column(nullable = false)
    private String name;

    /**
     * Текущее значение KPI.
     * Пример: 85.5 (проценты выполнения), 1000 (единицы производства).
     */
    @Column(nullable = false)
    private Double value;

    /**
     * Пороговое значение KPI.
     * Пример: 90.0 (если производительность падает ниже, требуется действие).
     */
    @Column(nullable = false)
    private Double threshold;

    /**
     * Дата и время начала действия KPI.
     */
    @Column(nullable = false)
    private LocalDateTime startDate;

    /**
     * Дата и время окончания действия KPI. Может быть null, если действие не ограничено.
     */
    private LocalDateTime endDate;

    /**
     * Флаг, указывающий, активен ли KPI.
     * Активный KPI доступен для отслеживания и отчётности.
     */
    @Column(nullable = false)
    private Boolean isActive;

    /**
     * Пользователь, которому принадлежит KPI.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /**
     * Набор отчетов, к которым относится данный KPI.
     */
    @ManyToMany(mappedBy = "kpis", fetch = FetchType.LAZY)
    private Set<Report> reports = new HashSet<>();
}
