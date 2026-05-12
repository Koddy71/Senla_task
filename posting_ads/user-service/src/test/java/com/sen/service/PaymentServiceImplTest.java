package com.sen.service;

import com.sen.client.AdServiceClient;
import com.sen.dto.internal.AdInternal;
import com.sen.dto.request.PaymentCreateRequest;
import com.sen.dto.response.PaymentResponse;
import com.sen.entity.Payment;
import com.sen.entity.User;
import com.sen.enums.PaymentStatus;
import com.sen.enums.Role;
import com.sen.exception.AdException;
import com.sen.exception.InsufficientBalanceException;
import com.sen.exception.NotOwnerException;
import com.sen.exception.PaymentException;
import com.sen.exception.UserNotFoundException;
import com.sen.mapper.PaymentMapper;
import com.sen.repository.PaymentRepository;
import com.sen.repository.UserRepository;
import com.sen.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private AdServiceClient adServiceClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User testUser;
    private UUID testPaymentId;
    private UUID testAdId;
    private static final BigDecimal PRICE_HOUR = new BigDecimal("10.00");

    @BeforeEach
    void setUp() {
        testAdId = UUID.randomUUID();
        testPaymentId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setLogin("buyer");
        testUser.setBalance(new BigDecimal("100.00"));
        testUser.setRole(Role.USER);
        testUser.setBlocked(false);
    }

    // ==================== CREATE PAYMENT ====================

    @Test
    void createPayment_shouldCreatePendingTransaction() {
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setAdId(testAdId);
        request.setHours(3);

        AdInternal ad = new AdInternal();
        ad.setId(testAdId);
        ad.setSellerId(testUser.getId());
        ad.setStatus("ACTIVE");

        when(userRepository.findByLogin("buyer")).thenReturn(Optional.of(testUser));
        when(adServiceClient.getAdById(testAdId)).thenReturn(ad);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(testPaymentId);
            return p;
        });

        PaymentResponse expectedResponse = new PaymentResponse();
        expectedResponse.setId(testPaymentId);
        expectedResponse.setStatus(PaymentStatus.PENDING);
        expectedResponse.setAmount(PRICE_HOUR.multiply(BigDecimal.valueOf(3)));
        expectedResponse.setHours(3);
        expectedResponse.setAdId(testAdId);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(expectedResponse);

        PaymentResponse response = paymentService.createPayment("buyer", request);

        assertNotNull(response);
        assertEquals(testPaymentId, response.getId());
        assertEquals(PaymentStatus.PENDING, response.getStatus());
        assertEquals(new BigDecimal("30.00"), response.getAmount());
        assertEquals(3, response.getHours());
        assertEquals(testAdId, response.getAdId());

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertEquals(PaymentStatus.PENDING, captor.getValue().getStatus());
        assertEquals(new BigDecimal("30.00"), captor.getValue().getAmount());
    }

    @Test
    void createPayment_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setAdId(testAdId);
        request.setHours(1);

        assertThrows(UserNotFoundException.class,
                () -> paymentService.createPayment("unknown", request));
    }

    @Test
    void createPayment_shouldThrowWhenAdInactive() {
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setAdId(testAdId);
        request.setHours(1);

        AdInternal ad = new AdInternal();
        ad.setStatus("INACTIVE");

        when(userRepository.findByLogin("buyer")).thenReturn(Optional.of(testUser));
        when(adServiceClient.getAdById(testAdId)).thenReturn(ad);

        assertThrows(AdException.class,
                () -> paymentService.createPayment("buyer", request));
    }

    @Test
    void createPayment_shouldThrowWhenUserIsNotSeller() {
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setAdId(testAdId);
        request.setHours(1);

        AdInternal ad = new AdInternal();
        ad.setSellerId(UUID.randomUUID()); // другой продавец
        ad.setStatus("ACTIVE");

        when(userRepository.findByLogin("buyer")).thenReturn(Optional.of(testUser));
        when(adServiceClient.getAdById(testAdId)).thenReturn(ad);

        assertThrows(NotOwnerException.class,
                () -> paymentService.createPayment("buyer", request));
    }

    // ==================== PROCESS PAYMENT ====================

    @Test
    void processPayment_shouldDeductBalanceAndSetSuccess() {
        Payment payment = new Payment();
        payment.setId(testPaymentId);
        payment.setUser(testUser);
        payment.setAdId(testAdId);
        payment.setAmount(new BigDecimal("30.00"));
        payment.setHours(3);
        payment.setStatus(PaymentStatus.PENDING);

        AdInternal ad = new AdInternal();
        ad.setId(testAdId);
        ad.setStatus("ACTIVE");

        when(paymentRepository.findById(testPaymentId)).thenReturn(Optional.of(payment));
        when(adServiceClient.getAdById(testAdId)).thenReturn(ad);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponse expectedResponse = new PaymentResponse();
        expectedResponse.setStatus(PaymentStatus.SUCCESS);
        expectedResponse.setProcessedAt(LocalDateTime.now());
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(expectedResponse);

        PaymentResponse response = paymentService.processPayment(testPaymentId, "buyer");

        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertNotNull(response.getProcessedAt());
        assertEquals(new BigDecimal("70.00"), testUser.getBalance());

        verify(userRepository).save(testUser);
        verify(paymentRepository, atLeastOnce()).save(any());
        verify(adServiceClient).promoteAd(testAdId, 3);
    }

    @Test
    void processPayment_shouldThrowWhenAlreadyProcessed() {
        Payment payment = new Payment();
        payment.setId(testPaymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setUser(testUser);

        when(paymentRepository.findById(testPaymentId)).thenReturn(Optional.of(payment));

        assertThrows(PaymentException.class,
                () -> paymentService.processPayment(testPaymentId, "buyer"));
    }

    @Test
    void processPayment_shouldThrowWhenPaymentNotOwnedByUser() {
        Payment payment = new Payment();
        payment.setId(testPaymentId);
        payment.setStatus(PaymentStatus.PENDING);
        User otherUser = new User();
        otherUser.setLogin("other");
        payment.setUser(otherUser);

        when(paymentRepository.findById(testPaymentId)).thenReturn(Optional.of(payment));

        assertThrows(PaymentException.class,
                () -> paymentService.processPayment(testPaymentId, "buyer"));
    }

    @Test
    void processPayment_shouldThrowInsufficientBalanceAndSetFailed() {
        testUser.setBalance(new BigDecimal("10.00"));

        Payment payment = new Payment();
        payment.setId(testPaymentId);
        payment.setUser(testUser);
        payment.setAmount(new BigDecimal("50.00"));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAdId(testAdId);

        AdInternal ad = new AdInternal();
        ad.setStatus("ACTIVE");

        when(paymentRepository.findById(testPaymentId)).thenReturn(Optional.of(payment));
        when(adServiceClient.getAdById(testAdId)).thenReturn(ad);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThrows(InsufficientBalanceException.class,
                () -> paymentService.processPayment(testPaymentId, "buyer"));

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        verify(paymentRepository).save(payment);
        verify(userRepository, never()).save(any());
        verify(adServiceClient, never()).promoteAd(any(), anyInt());
    }

    // ==================== GET TRANSACTIONS ====================

    @Test
    void getUserTransactions_shouldReturnList() {
        Payment p1 = new Payment();
        p1.setId(UUID.randomUUID());
        p1.setUser(testUser);
        p1.setStatus(PaymentStatus.SUCCESS);

        PaymentResponse resp = new PaymentResponse();
        resp.setStatus(PaymentStatus.SUCCESS);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(resp);

        when(userRepository.findByLogin("buyer")).thenReturn(Optional.of(testUser));
        when(paymentRepository.findUserId(testUser.getId())).thenReturn(List.of(p1));

        List<PaymentResponse> result = paymentService.getUserTransactions("buyer");

        assertEquals(1, result.size());
        assertEquals(PaymentStatus.SUCCESS, result.get(0).getStatus());
    }

    @Test
    void getTransactionsByAdId_shouldReturnList() {
        Payment p1 = new Payment();
        p1.setId(UUID.randomUUID());
        p1.setAdId(testAdId);
        p1.setStatus(PaymentStatus.PENDING);

        PaymentResponse resp = new PaymentResponse();
        resp.setAdId(testAdId);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(resp);

        AdInternal ad = new AdInternal();
        ad.setId(testAdId);
        ad.setSellerId(testUser.getId());
        ad.setStatus("ACTIVE");

        when(userRepository.findByLogin("buyer")).thenReturn(Optional.of(testUser));
        when(adServiceClient.getAdById(testAdId)).thenReturn(ad);
        when(paymentRepository.findAdId(testAdId)).thenReturn(List.of(p1));

        List<PaymentResponse> result = paymentService.getTransactionsByAdId(testAdId, "buyer");

        assertEquals(1, result.size());
        assertEquals(testAdId, result.get(0).getAdId());
    }

    @Test
    void getTransactionsByAdId_shouldThrowWhenUserIsNotSeller() {
        AdInternal ad = new AdInternal();
        ad.setId(testAdId);
        ad.setSellerId(UUID.randomUUID()); // другой продавец
        ad.setStatus("ACTIVE");

        when(userRepository.findByLogin("buyer")).thenReturn(Optional.of(testUser));
        when(adServiceClient.getAdById(testAdId)).thenReturn(ad);

        assertThrows(NotOwnerException.class,
                () -> paymentService.getTransactionsByAdId(testAdId, "buyer"));
    }
}