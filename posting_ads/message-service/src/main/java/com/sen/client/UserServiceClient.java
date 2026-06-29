package com.sen.client;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.sen.dto.internal.UserInternal;
import com.sen.exception.UserNotFoundException;
import com.sen.exception.UserServiceException;

@Component
public class UserServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public UserServiceClient(RestTemplate restTemplate, @Value("${user.service.url}") String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = serviceUrl;
    }

    public UserInternal getByLogin(String login) {
        try {
            String uri = UriComponentsBuilder
                    .fromUriString(serviceUrl)
                    .pathSegment("by-login", login)
                    .toUriString();

            HttpEntity<Void> entity = new HttpEntity<>(authHeaders());

            ResponseEntity<UserInternal> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, UserInternal.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Пользователь не найден по логину: {}", login);
            throw new UserNotFoundException("Пользователь не найден: " + login);

        } catch (RestClientException e) {
            logger.error("Ошибка при вызове user-service для получения пользователя по логину {}: {}", login,
                    e.getMessage(), e);
            throw new UserServiceException("User service недоступен: " + e.getMessage());
        }
    }

    public UserInternal getById(UUID id) {
        try {
            String uri = UriComponentsBuilder
                    .fromUriString(serviceUrl)
                    .pathSegment("by-id", id.toString())
                    .toUriString();
            HttpEntity<Void> entity = new HttpEntity<>(authHeaders());
            ResponseEntity<UserInternal> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, UserInternal.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Пользователь не найден по id: {}", id);
            throw new UserNotFoundException("Пользователь не найден: " + id);
        } catch (RestClientException e) {
            logger.error("Ошибка при вызове user-service для получения пользователя по id {}: {}", id, e.getMessage(),
                    e);
            throw new UserServiceException("User service недоступен: " + e.getMessage());
        }
    }

    public List<UserInternal> getByIds(Set<UUID> ids) {
        try {
            String uri = UriComponentsBuilder
                    .fromUriString(serviceUrl)
                    .pathSegment("batch")
                    .toUriString();
            HttpEntity<Set<UUID>> entity = new HttpEntity<>(ids, authHeaders());
            ResponseEntity<UserInternal[]> response = restTemplate.exchange(
                    uri, HttpMethod.POST, entity, UserInternal[].class);

            List<UserInternal> result = Arrays.asList(response.getBody());
            if (result.size() != ids.size()) {
                logger.error("Не все пользователи найдены. Запрошено: {}, получено: {}", ids.size(), result.size());
                throw new UserNotFoundException("Не все пользователи найдены");
            }

            return result;

        } catch (RestClientException e) {
            logger.error("Ошибка при вызове user-service для получения пользователей по ids {}: {}", ids,
                    e.getMessage(), e);
            throw new UserServiceException("User service недоступен: " + e.getMessage());
        }
    }

    // извлечение заголовка Authorization из текущего входящего HTTP-запроса и
    // создания нового объекта HttpHeaders с этим же заголовком
    private HttpHeaders authHeaders() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null || attrs.getRequest() == null) {
            throw new IllegalStateException("Нет текущего HTTP-запроса");
        }

        String auth = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || auth.isBlank()) {
            throw new IllegalStateException("В текущем запросе отсутствует заголовок Authorization.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, auth);
        return headers;
    }
}