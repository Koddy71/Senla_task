package com.sen.service;

import java.util.List;
import java.util.UUID;

import com.sen.dto.request.PaymentCreateRequest;
import com.sen.dto.response.PaymentResponse;

public interface PaymentService {

    /**
     * Создание платежа на продвижение (adId, hours).
     * Сумма = часы * 10.00, статус PENDING.
     */
    PaymentResponse createPayment(String myLogin, PaymentCreateRequest paymentRequest);

    /**
     * Обработка платежа (transactionId).
     * Списание баланса пользователя, при успехе — promotion через adServiceClient.
     */
    PaymentResponse processPayment(UUID transactionId, String myLogin);

    /**
     * История всех платежей текущего пользователя.
     */
    List<PaymentResponse> getUserTransactions(String myLogin);

    /**
     * Список платежей по объявлению (доступ только владельцу).
     */
    List<PaymentResponse> getTransactionsByAdId(UUID adId, String myLogin);
}
