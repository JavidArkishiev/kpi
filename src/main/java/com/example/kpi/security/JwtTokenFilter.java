package com.example.kpi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для проверки JWT токенов в каждом запросе.
 * <p>
 * Этот фильтр перехватывает каждый HTTP-запрос и проверяет наличие и валидность
 * JWT токена в заголовке Authorization. Если токен валиден, аутентификация пользователя
 * устанавливается в SecurityContext.
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Конструктор фильтра JWT.
     *
     * @param jwtTokenProvider Провайдер токенов для генерации и проверки JWT.
     */
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Основной метод фильтрации запросов.
     * Проверяет наличие токена в заголовке Authorization, валидирует его
     * и устанавливает аутентификацию в SecurityContext, если токен валиден.
     *
     * @param request     Объект HTTP-запроса.
     * @param response    Объект HTTP-ответа.
     * @param filterChain Цепочка фильтров для дальнейшей обработки запроса.
     * @throws ServletException Если возникает ошибка в процессе фильтрации.
     * @throws IOException      Если возникает ошибка ввода/вывода.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Извлечение заголовка Authorization
        String authHeader = request.getHeader("Authorization");


        // Проверка наличия токена и его формата
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Извлечение токена из заголовка
            if (jwtTokenProvider.validateToken(token)) { // Проверка валидности токена
                String username = jwtTokenProvider.getUsernameFromToken(token);
                // Извлечение имени пользователя из токена
                var roles = jwtTokenProvider.getAuthoritiesFromToken(token);
                var authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                        .toList();
                // Установка информации об аутентификации в SecurityContext
                JwtAuthenticationToken authentication = new JwtAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Передача запроса дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }
}
