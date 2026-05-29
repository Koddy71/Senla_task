package com.sen.client;

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

import com.sen.dto.internal.AdInternal;
import com.sen.exception.AdNotFoundException;
import com.sen.exception.AdServiceException;

@Component
public class AdServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(AdServiceClient.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public AdServiceClient(RestTemplate restTemplate, @Value("${ad.service.url}") String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = serviceUrl;
    }

    public void promoteAd(UUID adId, int hours) {
        try {
            String uri = UriComponentsBuilder
                    .fromUriString(serviceUrl)
                    .pathSegment(adId.toString(), "promote")
                    .queryParam("hours", hours)
                    .toUriString();

            HttpEntity<Void> entity = new HttpEntity<>(authHeaders());
            restTemplate.exchange(uri, HttpMethod.POST, entity, Void.class);
        } catch (RestClientException e) {
            logger.error("Ошибка при вызове ad-service для продвижения объявления {} на {} часов: {}", adId, hours,
                    e.getMessage(), e);
            throw new AdServiceException("Ad service недоступен: " + e.getMessage());
        }
    }

    public AdInternal getAdById(UUID adId) {
        try {
            String uri = UriComponentsBuilder
                    .fromUriString(serviceUrl)
                    .pathSegment(adId.toString())
                    .toUriString();

            HttpEntity<Void> entity = new HttpEntity<>(authHeaders());
            ResponseEntity<AdInternal> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, AdInternal.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Объявление не найдено: {}", adId);
            throw new AdNotFoundException("Объявление не найдено: " + adId);
        } catch (RestClientException e) {
            logger.error("Ошибка при вызове ad-service для получения объявления {}: {}", adId, e.getMessage(), e);
            throw new AdServiceException("Ad service недоступен: " + e.getMessage());
        }
    }

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