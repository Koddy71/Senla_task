package com.sen.service;

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
import com.sen.service.impl.PurchaseServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private AdRepository adRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PurchaseMapper purchaseMapper;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private UUID adId;
    private UUID purchaseId;
    private UUID buyerId;
    private UUID sellerId;
    private static final String BUYER_LOGIN = "buyer";
    private static final String SELLER_LOGIN = "seller";
    private Ad ad;
    private UserInternal buyer;
    private Purchase purchase;

    @BeforeEach
    void setUp() {
        adId = UUID.randomUUID();
        purchaseId = UUID.randomUUID();
        buyerId = UUID.randomUUID();
        sellerId = UUID.randomUUID();

        ad = new Ad();
        ad.setId(adId);
        ad.setSellerId(sellerId);
        ad.setStatus(AdStatus.ACTIVE);
        ad.setTitle("Test Ad");

        buyer = new UserInternal();
        buyer.setId(buyerId);
        buyer.setLogin(BUYER_LOGIN);
        buyer.setBlocked(false);

        purchase = new Purchase();
        purchase.setId(purchaseId);
        purchase.setAd(ad);
        purchase.setBuyerId(buyerId);
        purchase.setStatus(PurchaseStatus.COMPLETED);
        purchase.setCompletedAt(LocalDateTime.now());
        purchase.setScore(null);
        purchase.setComment(null);
        purchase.setReviewCreatedAt(null);
    }

    //  CREATE PURCHASE 

    @Test
    void createPurchase_shouldCreateSuccessfully() {
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        when(userServiceClient.getByLogin(BUYER_LOGIN)).thenReturn(buyer);
        when(adRepository.save(any(Ad.class))).thenReturn(ad);
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

        PurchaseResponse expectedResponse = new PurchaseResponse();
        expectedResponse.setId(purchaseId);
        expectedResponse.setBuyerLogin(BUYER_LOGIN);
        when(purchaseMapper.toResponse(any(Purchase.class), eq(BUYER_LOGIN))).thenReturn(expectedResponse);

        PurchaseResponse response = purchaseService.createPurchase(adId, BUYER_LOGIN);

        assertNotNull(response);
        assertEquals(purchaseId, response.getId());

        assertEquals(AdStatus.SOLD, ad.getStatus());
        verify(adRepository).save(ad);

        ArgumentCaptor<Purchase> captor = ArgumentCaptor.forClass(Purchase.class);
        verify(purchaseRepository).save(captor.capture());
        Purchase savedPurchase = captor.getValue();
        assertEquals(buyerId, savedPurchase.getBuyerId());
        assertEquals(ad, savedPurchase.getAd());
        assertEquals(PurchaseStatus.COMPLETED, savedPurchase.getStatus());
        assertNotNull(savedPurchase.getCompletedAt());
    }

    @Test
    void createPurchase_shouldThrowWhenAdNotFound() {
        when(adRepository.findById(adId)).thenReturn(Optional.empty());
        assertThrows(AdNotFoundException.class, () -> purchaseService.createPurchase(adId, BUYER_LOGIN));
        verify(adRepository, never()).save(any());
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void createPurchase_shouldThrowWhenAdNotActive() {
        ad.setStatus(AdStatus.SOLD);
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        assertThrows(AdNotActiveException.class, () -> purchaseService.createPurchase(adId, BUYER_LOGIN));
        verify(adRepository, never()).save(any());
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void createPurchase_shouldThrowWhenBuyerBlocked() {
        buyer.setBlocked(true);
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        when(userServiceClient.getByLogin(BUYER_LOGIN)).thenReturn(buyer);
        assertThrows(UserBlockedException.class, () -> purchaseService.createPurchase(adId, BUYER_LOGIN));
        verify(adRepository, never()).save(any());
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void createPurchase_shouldThrowWhenSelfPurchase() {
        ad.setSellerId(buyerId); // продавец = покупатель
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        when(userServiceClient.getByLogin(BUYER_LOGIN)).thenReturn(buyer);
        assertThrows(SelfPurchaseException.class, () -> purchaseService.createPurchase(adId, BUYER_LOGIN));
        verify(adRepository, never()).save(any());
        verify(purchaseRepository, never()).save(any());
    }

    //  ADD REVIEW 

    @Test
    void addReview_shouldAddSuccessfully() {
        ReviewRequest request = new ReviewRequest();
        request.setScore(5);
        request.setComment("Great product!");

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(userServiceClient.getByLogin(BUYER_LOGIN)).thenReturn(buyer);
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

        PurchaseResponse expectedResponse = new PurchaseResponse();
        expectedResponse.setId(purchaseId);
        expectedResponse.setScore(5);
        when(purchaseMapper.toResponse(purchase, BUYER_LOGIN)).thenReturn(expectedResponse);

        PurchaseResponse response = purchaseService.addReview(purchaseId, BUYER_LOGIN, request);

        assertNotNull(response);
        assertEquals(5, response.getScore());
        assertEquals(5, purchase.getScore());
        assertEquals("Great product!", purchase.getComment());
        assertNotNull(purchase.getReviewCreatedAt());
        verify(purchaseRepository).save(purchase);
    }

    @Test
    void addReview_shouldThrowWhenPurchaseNotFound() {
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());
        ReviewRequest request = new ReviewRequest();
        assertThrows(PurchaseNotFoundException.class,
                () -> purchaseService.addReview(purchaseId, BUYER_LOGIN, request));
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void addReview_shouldThrowWhenNotBuyer() {
        UserInternal otherUser = new UserInternal();
        otherUser.setId(UUID.randomUUID());
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(userServiceClient.getByLogin(BUYER_LOGIN)).thenReturn(otherUser);
        ReviewRequest request = new ReviewRequest();
        assertThrows(NotOwnerException.class, () -> purchaseService.addReview(purchaseId, BUYER_LOGIN, request));
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void addReview_shouldThrowWhenReviewAlreadyExists() {
        purchase.setScore(4); // уже есть оценка
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(userServiceClient.getByLogin(BUYER_LOGIN)).thenReturn(buyer);
        ReviewRequest request = new ReviewRequest();
        assertThrows(ReviewAlreadyExistsException.class,
                () -> purchaseService.addReview(purchaseId, BUYER_LOGIN, request));
        verify(purchaseRepository, never()).save(any());
    }

    //  GET MY PURCHASES 

    @Test
    void getMyPurchases_shouldReturnList() {
        when(userServiceClient.getByLogin(BUYER_LOGIN)).thenReturn(buyer);
        when(purchaseRepository.findByBuyerId(buyerId)).thenReturn(List.of(purchase));

        PurchaseResponse dto = new PurchaseResponse();
        dto.setId(purchaseId);
        when(purchaseMapper.toResponse(purchase, BUYER_LOGIN)).thenReturn(dto);

        List<PurchaseResponse> result = purchaseService.getMyPurchases(BUYER_LOGIN);

        assertEquals(1, result.size());
        assertEquals(purchaseId, result.get(0).getId());
    }

    //  GET SELLER RATING 

    @Test
    void getSellerRating_shouldReturnRating() {
        UserInternal seller = new UserInternal();
        seller.setId(sellerId);
        seller.setLogin(SELLER_LOGIN);
        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(seller);

       SellerRatingProjection projection = new SellerRatingProjection(4.5, 10);
        when(purchaseRepository.getSellerRating(sellerId)).thenReturn(projection);

        SellerRatingResponse response = purchaseService.getSellerRating(SELLER_LOGIN);

        assertEquals(SELLER_LOGIN, response.getSellerLogin());
        assertEquals(4.5, response.getAverageRating());
        assertEquals(10L, response.getTotalReviews());
    }

    @Test
    void getSellerRating_shouldReturnZeroWhenNoReviews() {
        UserInternal seller = new UserInternal();
        seller.setId(sellerId);
        seller.setLogin(SELLER_LOGIN);
        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(seller);

        SellerRatingProjection projection = new SellerRatingProjection(0.0, 0);
        when(purchaseRepository.getSellerRating(sellerId)).thenReturn(projection);

        SellerRatingResponse response = purchaseService.getSellerRating(SELLER_LOGIN);

        assertEquals(SELLER_LOGIN, response.getSellerLogin());
        assertEquals(0.0, response.getAverageRating());
        assertEquals(0, response.getTotalReviews());
    }

    //  GET PURCHASE BY ID 

    @Test
    void getPurchaseById_shouldReturnPurchase() {
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(userServiceClient.getById(buyerId)).thenReturn(buyer);
        PurchaseResponse dto = new PurchaseResponse();
        dto.setId(purchaseId);
        when(purchaseMapper.toResponse(purchase, BUYER_LOGIN)).thenReturn(dto);

        PurchaseResponse response = purchaseService.getPurchaseById(purchaseId);

        assertEquals(purchaseId, response.getId());
    }

    @Test
    void getPurchaseById_shouldThrowWhenNotFound() {
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());
        assertThrows(PurchaseNotFoundException.class, () -> purchaseService.getPurchaseById(purchaseId));
    }
}