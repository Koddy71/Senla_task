package com.sen.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sen.dto.internal.AdInternal;
import com.sen.exception.AdServiceException;

@Component
public class AdServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(AdServiceClient.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public AdServiceClient(RestTemplate restTemplate, @Value("${user.service.url}") String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = serviceUrl;
    }

    public void promoteAd(UUID adId, int hours) {
        String url = serviceUrl + adId + "/promote?hours=" + hours;
        try {
            restTemplate.postForObject(url, null, Void.class);
        } catch (RestClientException e) {
            logger.error("Ошибка при вызове ad-service: {}", e.getMessage(), e);
            throw new AdServiceException("Ad service недоступен: " + e);
        }
    }

    public AdInternal getAdById(UUID adId) {
        String url = serviceUrl + adId;
        try {
            return restTemplate.getForObject(url, AdInternal.class);
        } catch (RestClientException e) {
            logger.error("Ошибка при вызове ad-service для получения объявления {}: {}", adId, e.getMessage(), e);
            throw new AdServiceException("Ad service недоступен: " + e.getMessage());
        }
    }
}
