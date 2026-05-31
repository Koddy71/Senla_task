package com.sen.controller;

import com.sen.dto.internal.AdInternal;
import com.sen.service.AdService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalAdControllerTest {

    @Mock
    private AdService adService;

    @InjectMocks
    private InternalAdController controller;

    @Test
    void getAdById_shouldReturnAd() {
        UUID adId = UUID.randomUUID();
        AdInternal adInternal = new AdInternal();
        adInternal.setId(adId);
        when(adService.getAdById(adId)).thenReturn(adInternal);

        var result = controller.getAdById(adId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(adInternal, result.getBody());
    }
}