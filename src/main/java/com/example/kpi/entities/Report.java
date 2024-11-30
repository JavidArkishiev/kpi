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
 * Класс Report представляет отчёт, связанный с KPI и пользователем.
 */
@Getter
@Setter
@ToString(exclude = {"user", "kpis"})
@EqualsAndHashCode(exclude = {"user", "kpis"})
@Entity
@Table(name = "reports")
public class Report {

    /**
     * Уникальный идентификатор отчёта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата создания отчёта.
     */
    @Column(nullable = false)
    private LocalDateTime reportDate;

    /**
     * Пользователь, создавший отчёт.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Список KPI, связанных с этим отчётом.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "report_kpi",
            joinColumns = @JoinColumn(name = "report_id"),
            inverseJoinColumns = @JoinColumn(name = "kpi_id")
    )
    private Set<Kpi> kpis = new HashSet<>();

    /**
     * Добавляет KPI к отчёту.
     *
     * @param kpi KPI для добавления.
     */
    public void addKpi(Kpi kpi) {
        kpis.add(kpi);
        kpi.getReports().add(this);
    }

    /**
     * Удаляет KPI из отчёта.
     *
     * @param kpi KPI для удаления.
     */
    public void removeKpi(Kpi kpi) {
        kpis.remove(kpi);
        kpi.getReports().remove(this);
    }
}
