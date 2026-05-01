package com.sen.controller;

import com.sen.dto.request.PaymentCreateRequest;
import com.sen.dto.response.PaymentResponse;
import com.sen.enums.PaymentStatus;
import com.sen.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private PaymentController paymentController;

    private UserDetails createUserDetails(String login) {
        return new User(login, "password", Collections.emptyList());
    }

    @Test
    void createPayment_shouldReturn200() {
        UserDetails userDetails = createUserDetails("buyer");
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setAdId(UUID.randomUUID());
        request.setHours(5);

        PaymentResponse response = new PaymentResponse();
        response.setId(UUID.randomUUID());
        response.setStatus(PaymentStatus.PENDING);

        when(paymentService.createPayment(eq("buyer"), any(PaymentCreateRequest.class))).thenReturn(response);

        ResponseEntity<PaymentResponse> result = paymentController.createPayment(userDetails, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(PaymentStatus.PENDING, result.getBody().getStatus());
        verify(paymentService).createPayment("buyer", request);
    }

    @Test
    void processPayment_shouldReturn200() {
        UUID paymentId = UUID.randomUUID();
        PaymentResponse response = new PaymentResponse();
        response.setId(paymentId);
        response.setStatus(PaymentStatus.SUCCESS);

        when(paymentService.processPayment(paymentId)).thenReturn(response);

        ResponseEntity<PaymentResponse> result = paymentController.processPayment(paymentId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(PaymentStatus.SUCCESS, result.getBody().getStatus());
    }

    @Test
    void getMyTransactions_shouldReturnList() {
        UserDetails userDetails = createUserDetails("buyer");
        when(paymentService.getUserTransactions("buyer")).thenReturn(List.of(new PaymentResponse()));

        ResponseEntity<List<PaymentResponse>> result = paymentController.getMyTransactions(userDetails);

        assertEquals(1, result.getBody().size());
        verify(paymentService).getUserTransactions("buyer");
    }

    @Test
    void getTransactionsByAdId_shouldReturnList() {
        UUID adId = UUID.randomUUID();
        when(paymentService.getTransactionsByAdId(adId)).thenReturn(List.of(new PaymentResponse()));

        ResponseEntity<List<PaymentResponse>> result = paymentController.getTransactionsByAd(adId);

        assertEquals(1, result.getBody().size());
        verify(paymentService).getTransactionsByAdId(adId);
    }
}