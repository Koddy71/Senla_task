package ru.ilya.security;
//Ответ 401, если токен отсутствует/невалиден
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ru.ilya.exception.ApiError;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(){
        this.objectMapper=new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        ApiError body = new ApiError(HttpStatus.UNAUTHORIZED.value(),
            "Требуется аутентификация или JWT-токен недействителен");
        
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
