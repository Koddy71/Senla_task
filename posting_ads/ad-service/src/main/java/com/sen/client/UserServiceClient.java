package com.sen.client;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sen.dto.internal.UserInternal;
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
            URI uri = UriComponentsBuilder.fromUriString(serviceUrl)
                .pathSegment("by-login", login)
                .build()
                .toUri();
        return restTemplate.getForObject(uri, UserInternal.class);
        } catch (RestClientException e) {
            logger.error("Ошибка при вызове user-service для получения пользователя по логину {}: {}", login,
                    e.getMessage(), e);
            throw new UserServiceException("User service недоступен: " + e.getMessage());
        }
    }

    public UserInternal getById(UUID id) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(serviceUrl)
                    .pathSegment("by-id", id.toString())
                    .build()
                    .toUri();
            return restTemplate.getForObject(uri, UserInternal.class);
        } catch (RestClientException e) {
            logger.error("Ошибка при вызове user-service для получения пользователя по id {}: {}", id, e.getMessage(),
                    e);
            throw new UserServiceException("User service недоступен: " + e.getMessage());
        }
    }

    public List<UserInternal> getByIds(Set<UUID> ids) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(serviceUrl)
                    .pathSegment("batch")
                    .build()
                    .toUri();
            UserInternal[] users = restTemplate.postForObject(uri, ids, UserInternal[].class);
            return Arrays.asList(users);
        } catch (RestClientException e) {
            logger.error("Ошибка при вызове user-service для получения пользователей по ids {}: {}", ids,
                    e.getMessage(), e);
            throw new UserServiceException("User service недоступен: " + e.getMessage());
        }
    }
}