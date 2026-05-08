package com.sen.service;

import java.util.List;
import java.util.UUID;

import com.sen.dto.request.PaymentCreateRequest;
import com.sen.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(String userLogin, PaymentCreateRequest paymentRequest);

    PaymentResponse processPayment(UUID transactionId, String userLogin);

    List<PaymentResponse> getUserTransactions(String userLogin);

    List<PaymentResponse> getTransactionsByAdId(UUID adId, String userLogin);
}
