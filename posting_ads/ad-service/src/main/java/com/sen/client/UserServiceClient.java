package com.sen.client;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sen.dto.internal.UserInternal;
import com.sen.exception.UserServiceException;

@Component
public class UserServiceClient {
    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public UserServiceClient(RestTemplate restTemplate, @Value("${user.service.url}") String serviceUrl){
        this.restTemplate=restTemplate;
        this.serviceUrl=serviceUrl;
    }

    public UserInternal getByLogin(String login){
        try{
            return restTemplate.getForObject(serviceUrl + login, UserInternal.class);
        } catch (RestClientException e){
            throw new UserServiceException("User service недоступен: " + e.getMessage());
        }
    }

    public UserInternal getById(UUID id) {
        try {
            return restTemplate.getForObject(serviceUrl + id, UserInternal.class);
        } catch (RestClientException e) {
            throw new UserServiceException("User service недоступен: " + e.getMessage());
        }
    }

    public List<UserInternal> getByIds(Set<UUID> ids) {
        try {
            return Arrays.asList(restTemplate.postForObject(serviceUrl + "batch", ids, UserInternal[].class));
        } catch (RestClientException e) {
            throw new UserServiceException("User service недоступен: " + e.getMessage());
        }
    }
}
