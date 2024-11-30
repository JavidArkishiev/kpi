package com.example.kpi.security;

import com.example.kpi.entities.User;
import com.example.kpi.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * Провайдер для генерации и валидации JWT токенов.
 */
@Component
public class JwtTokenProvider {

    /**
     * Секретный ключ для подписи токенов.
     * Загружается из файла конфигурации для безопасности.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Время жизни токена в миллисекундах (1 час).
     */
    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    /**
     * Объект ключа для подписи токенов.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Base64 кодирование для безопасности
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Генерация JWT токена.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", user.getRoles().stream()
                        .map(Role::name)
                        .toList())
                .claim("userId", user.getId())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Извлечение имени пользователя из токена.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);


    }

    public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> roles = claims.get("roles", List.class);

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }


    /**
     * Проверка валидности токена.
     */
    public boolean validateToken(String token) {
        String usernameFromToken = getUsernameFromToken(token);
        return usernameFromToken != null
                && !usernameFromToken.isBlank()
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expirationDate.before(new Date());
    }

}
//NEW