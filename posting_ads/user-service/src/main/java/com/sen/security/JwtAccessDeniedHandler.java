package com.sen.security;

//Ответ 403, если пользователь аутентифицирован, но прав не хватает.
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sen.dto.response.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler{
    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectMapper objectMapper){
        this.objectMapper=objectMapper;
    }
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);;
        response.setCharacterEncoding("UTF-8");

        ErrorResponse body = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Недостаточно прав для выполнения операции");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
    
}
