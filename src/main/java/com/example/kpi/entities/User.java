package com.example.kpi.entities;

import com.example.kpi.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс User представляет базовую сущность пользователя системы.
 * <p>
 * Хранит информацию о пользователях, включая:
 * - Уникальный идентификатор.
 * - Электронную почту (логин).
 * - Пароль.
 * - Роли (CEO, Manager, Employee).
 * <p>
 * Используется в качестве базы для управления доступом и функциональностью
 * в зависимости от роли пользователя.
 */
@Getter
@Setter
@ToString(exclude = "roles")
@EqualsAndHashCode(exclude = "roles")
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email") // Уникальность email
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Электронная почта пользователя, выступает в качестве логина.
     * Уникальное значение, обязательное для заполнения.
     */
    @NotNull
    @Email
    @Size(max = 100)
    private String email;

    /**
     * Пароль пользователя.
     * Обязательное поле, минимальная длина — 8 символов.
     */
    @NotNull
    @Size(min = 8)
    private String password;

    /**
     * Роли пользователя.
     * Хранит роли, такие как CEO, Manager, Employee.
     * Используется для управления доступом к функционалу системы.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    /**
     * Добавляет роль пользователю.
     *
     * @param role Роль, которую необходимо добавить (например, CEO, Manager, Employee).
     */
    public void addRole(Role role) {
        roles.add(role);
    }

    /**
     * Удаляет роль у пользователя.
     *
     * @param role Роль, которую необходимо удалить.
     */
    public void removeRole(Role role) {
        roles.remove(role);
    }
}
