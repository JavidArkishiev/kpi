package com.example.kpi.repositories;

import com.example.kpi.entities.User;
import com.example.kpi.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по email.
     *
     * @param email Электронная почта пользователя.
     * @return Optional<User> - Найденный пользователь или пустой Optional.
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет, существует ли пользователь с указанным email.
     *
     * @param email Электронная почта пользователя.
     * @return true, если пользователь с таким email существует, иначе false.
     */
    boolean existsByEmail(String email);

    /**
     * Находит всех пользователей с заданной ролью.
     *
     * @param role Роль пользователя (например, CEO, Manager, Employee).
     * @return List<User> - Список пользователей с указанной ролью.
     */
    List<User> findAllByRoles(Role role);
}
