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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
import com.sen.exception.UserBlockedException;
import com.sen.exception.UserNotFoundException;
import com.sen.mapper.PaymentMapper;
import com.sen.rabbit.event.AdPromotionRequestedEvent;
import com.sen.rabbit.publisher.AdPromotionEventPublisher;
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
    private final AdPromotionEventPublisher adPromotionEventPublisher;

    private static final BigDecimal PRICE_HOUR = new BigDecimal("10.00");

    public PaymentServiceImpl(PaymentRepository paymentRepository,
            UserRepository userRepository,
            PaymentMapper paymentMapper,
            AdServiceClient adServiceClient,
            AdPromotionEventPublisher adPromotionEventPublisher) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.paymentMapper = paymentMapper;
        this.adServiceClient = adServiceClient;
        this.adPromotionEventPublisher = adPromotionEventPublisher;
    }

    @Override
    public PaymentResponse createPayment(String userLogin, PaymentCreateRequest paymentRequest) {
        logger.info("Запрос на создание платежа для пользователя {}, объявление {}, часов {}",
                userLogin, paymentRequest.getAdId(), paymentRequest.getHours());

        User user = findUserByLogin(userLogin);
        checkNotBlocked(user);
        AdInternal ad = getActiveAd(paymentRequest.getAdId());
        validateUserIsSeller(user, ad);

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

        Payment payment = findPaymentById(transactionId);
        validatePaymentOwnership(payment, userLogin);
        validatePaymentPending(payment);

        User user = payment.getUser();
        checkNotBlocked(user);

        AdInternal ad = getActiveAd(payment.getAdId());


        deductBalance(user, payment);

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProcessedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        AdPromotionRequestedEvent event = new AdPromotionRequestedEvent(
                saved.getId(),
                saved.getAdId(),
                saved.getHours(),
                saved.getUser().getLogin());
        adPromotionEventPublisher.publish(event);

        logger.info("Платёж успешно обработан, transactionId: {}, статус: {}", transactionId, saved.getStatus());
        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserTransactions(String userLogin) {
        logger.info("Запрос списка транзакций для пользователя {}", userLogin);
        User user = findUserByLogin(userLogin);
        checkNotBlocked(user);
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
        AdInternal ad = getActiveAd(adId);
        User user = findUserByLogin(userLogin);
        checkNotBlocked(user);
        validateUserIsSeller(user, ad);

        List<PaymentResponse> transactions = paymentRepository.findAdId(adId).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        logger.info("Найдено {} транзакций для объявления {}", transactions.size(), adId);
        return transactions;
    }

    private User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден", login);
                    return new UserNotFoundException("User not found: " + login);
                });
    }

    private void checkNotBlocked(User user) {
        if (user.isBlocked()) {
            logger.warn("Пользователь {} удалён", user.getLogin());
            throw new UserBlockedException(user.getLogin());
        }
    }

    private Payment findPaymentById(UUID transactionId) {
        return paymentRepository.findById(transactionId)
                .orElseThrow(() -> {
                    logger.error("Платёж не найден, transactionId: {}", transactionId);
                    return new PaymentException("PaymentTransaction not found: " + transactionId);
                });
    }

    private AdInternal getActiveAd(UUID adId) {
        AdInternal ad = adServiceClient.getAdById(adId);
        if (!"ACTIVE".equals(ad.getStatus())) {
            throw new AdException();
        }
        return ad;
    }

    private void validateUserIsSeller(User user, AdInternal ad) {
        if (!ad.getSellerId().equals(user.getId())) {
            throw new NotOwnerException();
        }
    }

    private void validatePaymentPending(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            logger.error("Платёж уже обработан, статус: {}", payment.getStatus());
            throw new PaymentException("PaymentTransaction already processed");
        }
    }

    private void validatePaymentOwnership(Payment payment, String userLogin) {
        if (!payment.getUser().getLogin().equals(userLogin)) {
            throw new PaymentException("Вы не можете обработать чужой платёж");
        }
    }

    private void deductBalance(User user, Payment payment) {
        if (user.getBalance().compareTo(payment.getAmount()) < 0) {
            logger.warn("Недостаточно средств для платежа {}, баланс: {}, требуется: {}",
                    payment.getId(), user.getBalance(), payment.getAmount());
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new InsufficientBalanceException("Insufficient balance for payment");
        }
        logger.info("Списание средств с баланса пользователя {}, сумма: {}", user.getLogin(), payment.getAmount());
        user.setBalance(user.getBalance().subtract(payment.getAmount()));
        userRepository.save(user);
    }
}