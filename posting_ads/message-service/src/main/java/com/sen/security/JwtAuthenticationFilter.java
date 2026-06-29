package com.sen.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            jwtAuthenticationEntryPoint.commence(request, response, null);
            return;
        }

        String token = header.substring(7).trim();

        if (!jwtTokenProvider.validateToken(token)) {
            jwtAuthenticationEntryPoint.commence(request, response, null);
            return;
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        Collection<? extends GrantedAuthority> authorities = jwtTokenProvider.extractAuthorities(token);

        UserDetails userDetails = new User(username, "", authorities);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                authorities);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
