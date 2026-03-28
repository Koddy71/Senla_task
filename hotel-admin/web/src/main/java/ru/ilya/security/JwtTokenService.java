package ru.ilya.security;
// Сервис генерации и проверки JWT
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenService {
    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenService(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String login, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirationMs);

        List<String> authorityNames;
        if (authorities == null) {
            authorityNames = List.of();
        } else {
            authorityNames = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        }

        return Jwts.builder()
                .setSubject(login)
                .claim("authorities", authorityNames)
                .setIssuedAt(now)
                .setExpiration(expiresAt)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }


    // Метод извлекает из переданного JWT-токена список прав
    // этого пользователя и возвращает их в SimpleGrantedAuthority
    public Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        Object claim = parseClaims(token).getBody().get("authorities");
        if (!(claim instanceof Collection<?>)) {
            return Collections.emptyList();
        }
        Collection<?> values = (Collection<?>) claim;
        return values.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}
