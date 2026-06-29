package com.sen.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Configuration
@EnableWebMvc
@EnableMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = "com.sen.controller")
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public ObjectMapper objectMapper() { // сериализация LocalDateTime в DTO
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        mapper.registerModule(module);
        return mapper;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) { // настраиваем конвертор
                                                                                       // скастомным ObjectMapper
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        converters.add(converter);
    }

}
