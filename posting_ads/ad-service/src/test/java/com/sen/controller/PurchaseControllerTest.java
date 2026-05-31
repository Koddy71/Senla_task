package com.sen.controller;

import com.sen.dto.request.ReviewRequest;
import com.sen.dto.response.PurchaseResponse;
import com.sen.dto.response.SellerRatingResponse;
import com.sen.service.PurchaseService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    @Mock
    private PurchaseService purchaseService;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private PurchaseController controller;

    private static final String USER_LOGIN = "buyer";
    private final UUID adId = UUID.randomUUID();
    private final UUID purchaseId = UUID.randomUUID();
    private final String sellerLogin = "seller";

    @Test
    void create_shouldReturnOk() {
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);
        PurchaseResponse responseDto = new PurchaseResponse();
        responseDto.setId(purchaseId);
        when(purchaseService.createPurchase(adId, USER_LOGIN)).thenReturn(responseDto);

        var result = controller.create(adId, userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
    }

    @Test
    void addReview_shouldReturnOk() {
        ReviewRequest request = new ReviewRequest();
        request.setScore(5);
        request.setComment("Good");

        when(userDetails.getUsername()).thenReturn(USER_LOGIN);
        PurchaseResponse responseDto = new PurchaseResponse();
        responseDto.setId(purchaseId);
        when(purchaseService.addReview(purchaseId, USER_LOGIN, request)).thenReturn(responseDto);

        var result = controller.addReview(purchaseId, userDetails, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
    }

    @Test
    void getMyPurchases_shouldReturnOk() {
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);
        List<PurchaseResponse> responses = List.of(new PurchaseResponse(), new PurchaseResponse());
        when(purchaseService.getMyPurchases(USER_LOGIN)).thenReturn(responses);

        var result = controller.getMyPurchases(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responses, result.getBody());
    }

    @Test
    void getById_shouldReturnOk() {
        PurchaseResponse responseDto = new PurchaseResponse();
        responseDto.setId(purchaseId);
        when(purchaseService.getPurchaseById(purchaseId)).thenReturn(responseDto);

        var result = controller.getById(purchaseId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
    }

    @Test
    void getRating_shouldReturnOk() {
        SellerRatingResponse ratingResponse = new SellerRatingResponse(sellerLogin, 4.5, 10L);
        when(purchaseService.getSellerRating(sellerLogin)).thenReturn(ratingResponse);

        var result = controller.getRating(sellerLogin);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(ratingResponse, result.getBody());
    }
}