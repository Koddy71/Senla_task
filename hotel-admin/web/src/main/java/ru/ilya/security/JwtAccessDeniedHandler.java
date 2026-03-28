package ru.ilya.security;
//Ответ 403, если пользователь аутентифицирован, но прав не хватает.
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import ru.ilya.exception.ApiError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiError body = new ApiError(HttpStatus.FORBIDDEN.value(),
                "Недостаточно прав для выполнения операции");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}