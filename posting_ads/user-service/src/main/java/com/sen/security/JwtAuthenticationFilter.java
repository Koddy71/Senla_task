package com.sen.security;

//перехватывает каждый HTTP-запрос, извлекает и проверяет JWT.
//если токен валиден, создаёт и устанавливает объект Authentication в SecurityContext, 
//делая пользователя аутентифицированным для последующей обработки запроса
import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtTokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            UserDetailsService userDetailsService) {
        this.jwtTokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Override           //отключает проверку токена
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "/api/users/register".equals(path) || "/api/users/login".equals(path);
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

        if (!jwtTokenProvider.validateToken(token)) {
            SecurityContextHolder.clearContext();
            jwtAuthenticationEntryPoint.commence(request, response,
                    new BadCredentialsException("Некорректный JWT-токен"));
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtTokenProvider.getUsernameFromToken(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}