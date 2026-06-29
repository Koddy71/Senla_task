package com.sen.controller;

import com.sen.dto.request.AdCreateRequest;
import com.sen.dto.request.AdFilterRequest;
import com.sen.dto.request.AdUpdateRequest;
import com.sen.dto.response.AdDetailResponse;
import com.sen.dto.response.AdResponse;
import com.sen.service.AdService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdControllerTest {

    @Mock
    private AdService adService;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AdController controller;

    private static final String USER_LOGIN = "testUser";
    private final UUID adId = UUID.randomUUID();

    @Test
    void create_shouldReturnCreated() {
        AdCreateRequest request = new AdCreateRequest();
        AdDetailResponse responseDto = new AdDetailResponse();
        responseDto.setId(adId);
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);
        when(adService.createAd(USER_LOGIN, request)).thenReturn(responseDto);

        var result = controller.create(userDetails, request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(adService).createAd(USER_LOGIN, request);
    }

    @Test
    void update_shouldReturnOk() {
        UUID id = adId;
        AdUpdateRequest request = new AdUpdateRequest();
        AdDetailResponse responseDto = new AdDetailResponse();
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);
        when(adService.updateAd(id, USER_LOGIN, request)).thenReturn(responseDto);

        var result = controller.update(id, userDetails, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(adService).updateAd(id, USER_LOGIN, request);
    }

    @Test
    void delete_shouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);
        doNothing().when(adService).deleteAd(adId, USER_LOGIN);

        var result = controller.delete(adId, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(adService).deleteAd(adId, USER_LOGIN);
    }

    @Test
    void getMyAds_shouldReturnOk() {
        List<AdDetailResponse> responses = List.of(new AdDetailResponse());
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);
        when(adService.getMyAds(USER_LOGIN)).thenReturn(responses);

        var result = controller.getMyAds(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responses, result.getBody());
        verify(adService).getMyAds(USER_LOGIN);
    }

    @Test
    void search_shouldReturnOk() {
        AdFilterRequest filter = new AdFilterRequest();
        List<AdResponse> responses = List.of(new AdResponse());
        when(adService.searchAds(filter)).thenReturn(responses);

        var result = controller.search(filter);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responses, result.getBody());
        verify(adService).searchAds(filter);
    }

    @Test
    void getBySeller_shouldReturnOk() {
        String login = "seller";
        List<AdResponse> responses = List.of(new AdResponse());
        when(adService.getAdsBySeller(login)).thenReturn(responses);

        var result = controller.getBySeller(login);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responses, result.getBody());
        verify(adService).getAdsBySeller(login);
    }

    @Test
    void block_shouldReturnNoContent() {
        doNothing().when(adService).blockAd(adId);

        var result = controller.block(adId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(adService).blockAd(adId);
    }

    @Test
    void unblock_shouldReturnNoContent() {
        doNothing().when(adService).unblockAd(adId);

        var result = controller.unblock(adId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(adService).unblockAd(adId);
    }
}