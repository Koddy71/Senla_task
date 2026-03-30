//перехватывает каждый HTTP-запрос, извлекает и проверяет JWT.
//если токен валиден, создаёт и устанавливает объект Authentication в SecurityContext, 
//делая пользователя аутентифицированным для последующей обработки запроса
package ru.ilya.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtTokenService = jwtTokenService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || "/api/auth/login".equals(path)
                || "/error".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();

        if (!jwtTokenService.isValid(token)) {
            SecurityContextHolder.clearContext();
            jwtAuthenticationEntryPoint.commence(request, response,
                    new BadCredentialsException("Некорректный JWT-токен"));
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtTokenService.extractUsername(token);
            Collection<? extends GrantedAuthority> authorities = jwtTokenService.extractAuthorities(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
                    authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}