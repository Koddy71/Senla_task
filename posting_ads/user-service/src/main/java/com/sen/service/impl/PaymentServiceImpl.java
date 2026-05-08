package com.sen.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sen.client.AdServiceClient;
import com.sen.dto.internal.AdInternal;
import com.sen.dto.request.PaymentCreateRequest;
import com.sen.dto.response.PaymentResponse;
import com.sen.entity.Payment;
import com.sen.entity.User;
import com.sen.enums.PaymentStatus;
import com.sen.exception.AdException;
import com.sen.exception.InsufficientBalanceException;
import com.sen.exception.NotOwnerException;
import com.sen.exception.PaymentException;
import com.sen.exception.UserNotFoundException;
import com.sen.mapper.PaymentMapper;
import com.sen.repository.PaymentRepository;
import com.sen.repository.UserRepository;
import com.sen.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final AdServiceClient adServiceClient;

    private static final BigDecimal PRICE_HOUR = new BigDecimal("10.00");

    public PaymentServiceImpl(PaymentRepository paymentRepository,
            UserRepository userRepository,
            PaymentMapper paymentMapper,
            AdServiceClient adServiceClient) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.paymentMapper = paymentMapper;
        this.adServiceClient = adServiceClient;
    }

    @Override
    public PaymentResponse createPayment(String userLogin, PaymentCreateRequest paymentRequest) {
        logger.info("Запрос на создание платежа для пользователя {}, объявление {}, часов {}",
                userLogin, paymentRequest.getAdId(), paymentRequest.getHours());
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден при создании платежа", userLogin);
                    return new UserNotFoundException("User not found: " + userLogin);
                });

        AdInternal ad = adServiceClient.getAdById(paymentRequest.getAdId());

        if (!"ACTIVE".equals(ad.getStatus())) {
            throw new AdException();
        }
        if (!ad.getSellerId().equals(user.getId())) {
            throw new NotOwnerException();
        }


        BigDecimal amount = PRICE_HOUR.multiply(BigDecimal.valueOf(paymentRequest.getHours()));
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAdId(paymentRequest.getAdId());
        payment.setHours(paymentRequest.getHours());
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(payment);
        logger.info("Платёж успешно создан, transactionId: {}, сумма: {}", saved.getId(), amount);
        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(noRollbackFor = InsufficientBalanceException.class)
    public PaymentResponse processPayment(UUID transactionId, String userLogin) {
        logger.info("Запрос на обработку платежа, transactionId: {}", transactionId);
        Payment payment = paymentRepository.findById(transactionId)
                .orElseThrow(() -> {
                    logger.error("Платёж не найден, transactionId: {}", transactionId);
                    return new PaymentException("PaymentTransaction not found: " + transactionId);
                });

        if (!payment.getUser().getLogin().equals(userLogin)) {
            throw new PaymentException("Вы не можете обработать чужой платёж");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            logger.error("Платёж уже обработан, transactionId: {}, текущий статус: {}",
                    transactionId, payment.getStatus());
            throw new PaymentException("PaymentTransaction already processed");
        }

        AdInternal ad = adServiceClient.getAdById(payment.getAdId());

        if (!"ACTIVE".equals(ad.getStatus())) {
            throw new AdException();
        }

        User user = payment.getUser();

        if (user.getBalance().compareTo(payment.getAmount()) < 0) {
            logger.warn("Недостаточно средств для платежа {}, баланс: {}, требуется: {}",
                    transactionId, user.getBalance(), payment.getAmount());
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new InsufficientBalanceException("Insufficient balance for payment");
        }

        logger.info("Списание средств с баланса пользователя {}, сумма: {}", user.getLogin(), payment.getAmount());
        user.setBalance(user.getBalance().subtract(payment.getAmount()));
        userRepository.save(user);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProcessedAt(LocalDateTime.now());

        logger.info("Вызов сервиса продвижения объявления adId: {}, часов: {}", payment.getAdId(), payment.getHours());
        adServiceClient.promoteAd(payment.getAdId(), payment.getHours());

        Payment saved = paymentRepository.save(payment);
        logger.info("Платёж успешно обработан, transactionId: {}, статус: {}", transactionId, saved.getStatus());
        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserTransactions(String userLogin) {
        logger.info("Запрос списка транзакций для пользователя {}", userLogin);
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден при получении транзакций", userLogin);
                    return new UserNotFoundException("User not found: " + userLogin);
                });
        List<PaymentResponse> transactions = paymentRepository.findUserId(user.getId()).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        logger.info("Найдено {} транзакций для пользователя {}", transactions.size(), userLogin);
        return transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getTransactionsByAdId(UUID adId, String userLogin) {
        logger.info("Запрос списка транзакций для объявления {}", adId);
        AdInternal ad = adServiceClient.getAdById(adId);

        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден", userLogin);
                    return new UserNotFoundException("User not found: " + userLogin);
                });
        
        if (!ad.getSellerId().equals(user.getId())) {
            throw new NotOwnerException();
        }

        List<PaymentResponse> transactions = paymentRepository.findAdId(adId).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        logger.info("Найдено {} транзакций для объявления {}", transactions.size(), adId);
        return transactions;
    }
}   