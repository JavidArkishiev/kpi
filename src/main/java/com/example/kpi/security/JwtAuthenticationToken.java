package com.example.kpi.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Пользовательский класс для хранения аутентификационных данных JWT.
 * <p>
 * Используется для передачи информации о пользователе, прошедшем аутентификацию с использованием JWT.
 * Наследуется от {@link AbstractAuthenticationToken} для интеграции со Spring Security.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * Уникальный идентификатор пользователя (например, email или имя пользователя).
     */
    private final Object principal;

    /**
     * Учетные данные пользователя (обычно null для JWT).
     */
    private Object credentials;

    /**
     * Конструктор для создания аутентификационного токена.
     *
     * @param principal   Уникальный идентификатор пользователя (например, email или username).
     * @param credentials Учетные данные (необязательно, обычно null для JWT).
     * @param authorities Коллекция ролей или прав пользователя.
     */
    public JwtAuthenticationToken(Object principal, Object credentials,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities); // Передаём роли в родительский класс
        this.principal = principal; // Устанавливаем идентификатор пользователя
        this.credentials = credentials; // Устанавливаем учетные данные
        setAuthenticated(true); // Помечаем, что пользователь аутентифицирован
    }

    /**
     * Возвращает учетные данные пользователя.
     *
     * @return Объект учетных данных (обычно null для JWT).
     */
    @Override
    public Object getCredentials() {
        return credentials;
    }

    /**
     * Возвращает идентификатор пользователя (principal).
     *
     * @return Объект principal, представляющий уникальный идентификатор пользователя.
     */
    @Override
    public Object getPrincipal() {
        return principal;
    }
}
