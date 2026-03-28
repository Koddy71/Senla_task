package ru.ilya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ru.ilya.security.JwtAccessDeniedHandler;
import ru.ilya.security.JwtAuthenticationEntryPoint;
import ru.ilya.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAccessDeniedHandler accessDeniedHandler,
            JwtAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**", "/api/services/**", "/api/prices")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/guests/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/rooms/**", "/api/services/**", "/api/guests/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**", "/api/services/**", "/api/guests/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/rooms/**", "/api/services/**", "/api/guests/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}