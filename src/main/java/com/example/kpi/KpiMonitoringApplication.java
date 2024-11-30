package com.example.kpi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.example.kpi.entities")
public class KpiMonitoringApplication {

    public static void main(String[] args) {

        SpringApplication.run(KpiMonitoringApplication.class, args);

        // TODO Проект выглядит неплохо, прозрачно, но по объёму не хватает сути - бизнес-логики. Добавьте функционал:
        // 1) Пользователь может вносить данные о своём KPI. Предположим, что в клиентском приложении он пишет количество деталей, а на Ваш бэкенд данные приходят в виде уже посчитанного значения поля value для KPI.
        // Данные пользователь вносит каждый день, когда работает.
        // 2) Менеджер может создавать разные KPI для подчинённых сотрудников и делать репорты по ним.
        // 3) CEO может смотреть KPI по менеджерам. KPI менеджеров рассчитывается исходя из KPI сотрудников.
    }

}
