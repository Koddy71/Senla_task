package com.sen.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sen.dto.request.PaymentCreateRequest;
import com.sen.dto.response.PaymentResponse;
import com.sen.entity.Payment;
import com.sen.entity.User;
import com.sen.enums.PaymentStatus;
import com.sen.exception.InsufficientBalanceException;
import com.sen.exception.PaymentException;
import com.sen.exception.UserNotFoundException;
import com.sen.mapper.PaymentMapper;
import com.sen.repository.PaymentRepository;
import com.sen.repository.UserRepository;
import com.sen.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    private static final BigDecimal PRICE_HOUR = new  BigDecimal("10.00");

    public PaymentServiceImpl(PaymentRepository paymentRepository,
            UserRepository userRepository,
            PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.userRepository=userRepository;
        this.paymentMapper=paymentMapper;
    }

    @Override
    public PaymentResponse createPayment(String userLogin, PaymentCreateRequest paymentRequest) {
        User user = userRepository.findByLogin(userLogin)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userLogin));
        BigDecimal amount = PRICE_HOUR.multiply(BigDecimal.valueOf(paymentRequest.getHours()));
        Payment payment = new Payment();
        payment.setUser(user);    
        payment.setAdId(paymentRequest.getAdId());
        payment.setHours(paymentRequest.getHours());
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    @Override
    public PaymentResponse processPayment(UUID transactionId) {
        Payment payment = paymentRepository.findById(transactionId)
            .orElseThrow(() -> new PaymentException("PaymentTransaction not found: " + transactionId));
        
        if (payment.getStatus() != PaymentStatus.PENDING){
            throw new PaymentException("PaymentTransaction already processed");
        }

        User user = payment.getUser();
        
        if (user.getBalance().compareTo(payment.getAmount())<0){
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new InsufficientBalanceException("Insufficient balance for payment");
        }

        user.setBalance(user.getBalance().subtract(payment.getAmount()));
        userRepository.save(user);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProcessedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserTransactions(String userLogin) {
        User user = userRepository.findByLogin(userLogin)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userLogin));
        return paymentRepository.findUserId(user.getId()).stream()
            .map(paymentMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getTransactionsByAdId(UUID adId) {
        return paymentRepository.findAdId(adId).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

}
