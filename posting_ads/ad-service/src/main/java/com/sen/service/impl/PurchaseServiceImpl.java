package com.sen.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sen.client.UserServiceClient;
import com.sen.dto.internal.UserInternal;
import com.sen.dto.request.ReviewRequest;
import com.sen.dto.response.PurchaseResponse;
import com.sen.dto.response.SellerRatingResponse;
import com.sen.entity.Ad;
import com.sen.entity.Purchase;
import com.sen.enums.AdStatus;
import com.sen.enums.PurchaseStatus;
import com.sen.exception.AdNotActiveException;
import com.sen.exception.AdNotFoundException;
import com.sen.exception.NotOwnerException;
import com.sen.exception.PurchaseNotFoundException;
import com.sen.exception.ReviewAlreadyExistsException;
import com.sen.exception.SelfPurchaseException;
import com.sen.exception.UserBlockedException;
import com.sen.mapper.PurchaseMapper;
import com.sen.repository.AdRepository;
import com.sen.repository.PurchaseRepository;
import com.sen.repository.SellerRatingProjection;
import com.sen.service.PurchaseService;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    private final PurchaseRepository purchaseRepository;
    private final AdRepository adRepository;
    private final UserServiceClient userServiceClient;
    private final PurchaseMapper purchaseMapper;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository,
            AdRepository adRepository,
            UserServiceClient userServiceClient,
            PurchaseMapper purchaseMapper) {
        this.purchaseRepository = purchaseRepository;
        this.adRepository = adRepository;
        this.userServiceClient = userServiceClient;
        this.purchaseMapper = purchaseMapper;
    }

    @Override
    public PurchaseResponse createPurchase(UUID adId, String buyerLogin) {
        logger.info("Запрос на покупку объявления {} от покупателя {}", adId, buyerLogin);

        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> {
                    logger.error("Объявление {} не найдено", adId);
                    return new AdNotFoundException(adId);
                });

        if (ad.getStatus() != AdStatus.ACTIVE) {
            logger.error("Объявление {} не активно, статус: {}", adId, ad.getStatus());
            throw new AdNotActiveException();
        }

        UserInternal buyer = getUserByLogin(buyerLogin);
        if (buyer.isBlocked()) {
            logger.error("Покупатель {} заблокирован", buyerLogin);
            throw new UserBlockedException(buyerLogin);
        }

        if (ad.getSellerId().equals(buyer.getId())) {
            logger.error("Покупатель {} является продавцом объявления {}", buyerLogin, adId);
            throw new SelfPurchaseException();
        }

        ad.setStatus(AdStatus.SOLD);
        adRepository.save(ad);

        Purchase purchase = new Purchase();
        purchase.setAd(ad);
        purchase.setBuyerId(buyer.getId());
        purchase.setStatus(PurchaseStatus.COMPLETED);
        purchase.setCompletedAt(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);
        logger.info("Покупка успешно создана: id {}, объявление {}, покупатель {}", saved.getId(), adId, buyerLogin);
        return purchaseMapper.toResponse(saved, buyerLogin);
    }

    @Override
    public PurchaseResponse addReview(UUID purchaseId, String buyerLogin, ReviewRequest request) {
        logger.info("Запрос на добавление отзыва к покупке {} от покупателя {}", purchaseId, buyerLogin);

        Purchase purchase = findPurchaseById(purchaseId);
        UserInternal buyer = getUserByLogin(buyerLogin);

        if (!purchase.getBuyerId().equals(buyer.getId())) {
            logger.error("Пользователь {} не является покупателем покупки {}", buyerLogin, purchaseId);
            throw new NotOwnerException();
        }

        if (purchase.getScore() != null) {
            logger.error("Отзыв для покупки {} уже существует", purchaseId);
            throw new ReviewAlreadyExistsException();
        }

        purchase.setScore(request.getScore());
        purchase.setComment(request.getComment());
        purchase.setReviewCreatedAt(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);
        logger.info("Отзыв успешно добавлен к покупке {}", purchaseId);
        return purchaseMapper.toResponse(saved, buyerLogin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseResponse> getMyPurchases(String buyerLogin) {
        logger.info("Запрос списка покупок для пользователя {}", buyerLogin);
        UserInternal buyer = getUserByLogin(buyerLogin);

        List<PurchaseResponse> responses = purchaseRepository.findByBuyerId(buyer.getId()).stream()
                .map(purchase -> purchaseMapper.toResponse(purchase, buyerLogin))
                .collect(Collectors.toList());

        logger.info("Найдено {} покупок для пользователя {}", responses.size(), buyerLogin);
        return responses;
    }

    @Override
    public SellerRatingResponse getSellerRating(String sellerLogin) {
        logger.info("Запрос рейтинга продавца {}", sellerLogin);
        UserInternal seller = getUserByLogin(sellerLogin);

        SellerRatingProjection projection = purchaseRepository.getSellerRating(seller.getId());
        Double avg = projection.getAverageScore();
        Long total = projection.getTotalReviews();

        logger.info("Рейтинг продавца {}: средний балл {}, количество отзывов {}", sellerLogin, avg, total);
        return new SellerRatingResponse(sellerLogin, avg, total);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse getPurchaseById(UUID purchaseId) {
        logger.info("Запрос покупки по id {}", purchaseId);
        Purchase purchase = findPurchaseById(purchaseId);
        UserInternal buyer = userServiceClient.getById(purchase.getBuyerId());
        logger.info("Покупка {} найдена, покупатель {}", purchaseId, buyer.getLogin());
        return purchaseMapper.toResponse(purchase, buyer.getLogin());
    }

    private Purchase findPurchaseById(UUID purchaseId) {
        return purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> {
                    logger.error("Покупка с id {} не найдена", purchaseId);
                    return new PurchaseNotFoundException(purchaseId);
                });
    }

    private UserInternal getUserByLogin(String login) {
        return userServiceClient.getByLogin(login);
    }
}