package com.sen.service;

import com.sen.client.UserServiceClient;
import com.sen.dto.internal.AdInternal;
import com.sen.dto.internal.UserInternal;
import com.sen.dto.request.AdCreateRequest;
import com.sen.dto.request.AdFilterRequest;
import com.sen.dto.request.AdUpdateRequest;
import com.sen.dto.response.AdDetailResponse;
import com.sen.dto.response.AdResponse;
import com.sen.entity.Ad;
import com.sen.enums.AdStatus;
import com.sen.exception.AdNotFoundException;
import com.sen.exception.NotOwnerException;
import com.sen.exception.UserBlockedException;
import com.sen.mapper.AdMapper;
import com.sen.repository.AdRepository;
import com.sen.service.impl.AdServiceImpl;

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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {

    @Mock
    private AdRepository adRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private AdMapper adMapper;

    @InjectMocks
    private AdServiceImpl adService;

    private static final String SELLER_LOGIN = "seller";
    private static final String SELLER_FULLNAME = "Seller Name";
    private UUID sellerId;
    private UUID adId;
    private UserInternal seller;
    private Ad ad;

    @BeforeEach
    void setUp() {
        sellerId = UUID.randomUUID();
        adId = UUID.randomUUID();

        seller = new UserInternal();
        seller.setId(sellerId);
        seller.setLogin(SELLER_LOGIN);
        seller.setFullname(SELLER_FULLNAME);
        seller.setBlocked(false);

        ad = new Ad();
        ad.setId(adId);
        ad.setSellerId(sellerId);
        ad.setStatus(AdStatus.ACTIVE);
        ad.setTitle("Test Ad");
        ad.setDescription("Description");
        ad.setPrice(new java.math.BigDecimal("100.00"));
        ad.setCategory("Electronics");
        ad.setPromotedUntil(null);
    }

    @Test
    void createAd_shouldCreateAndReturnDetail() {
        AdCreateRequest request = new AdCreateRequest();
        request.setTitle("New Ad");
        request.setDescription("New Description");
        request.setPrice(new java.math.BigDecimal("50.00"));
        request.setCategory("Books");

        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(seller);

        Ad newAd = new Ad();
        newAd.setSellerId(sellerId);
        newAd.setStatus(AdStatus.ACTIVE);
        newAd.setPromotedUntil(null);
        when(adMapper.toEntity(request)).thenReturn(newAd);

        Ad savedAd = new Ad();
        savedAd.setId(adId);
        savedAd.setSellerId(sellerId);
        savedAd.setStatus(AdStatus.ACTIVE);
        when(adRepository.save(any(Ad.class))).thenReturn(savedAd);

        AdDetailResponse expectedResponse = new AdDetailResponse();
        expectedResponse.setId(adId);
        expectedResponse.setSellerName(SELLER_FULLNAME);
        when(adMapper.toDetailResponse(savedAd, SELLER_FULLNAME)).thenReturn(expectedResponse);

        AdDetailResponse response = adService.createAd(SELLER_LOGIN, request);

        assertNotNull(response);
        assertEquals(adId, response.getId());
        assertEquals(SELLER_FULLNAME, response.getSellerName());

        ArgumentCaptor<Ad> captor = ArgumentCaptor.forClass(Ad.class);
        verify(adRepository).save(captor.capture());
        Ad captured = captor.getValue();
        assertEquals(sellerId, captured.getSellerId());
        assertEquals(AdStatus.ACTIVE, captured.getStatus());
        assertNull(captured.getPromotedUntil());
    }

    @Test
    void createAd_shouldThrowWhenUserBlocked() {
        seller.setBlocked(true);
        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(seller);
        AdCreateRequest request = new AdCreateRequest();
        assertThrows(UserBlockedException.class, () -> adService.createAd(SELLER_LOGIN, request));
        verify(adRepository, never()).save(any());
    }

    @Test
    void updateAd_shouldUpdateFields() {
        AdUpdateRequest request = new AdUpdateRequest();
        request.setTitle("Updated Title");
        request.setPrice(new java.math.BigDecimal("200.00"));

        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(seller);
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        doNothing().when(adMapper).updateEntity(request, ad);
        when(adRepository.save(ad)).thenReturn(ad);

        AdDetailResponse expectedResponse = new AdDetailResponse();
        expectedResponse.setId(adId);
        expectedResponse.setTitle("Updated Title");
        expectedResponse.setSellerName(SELLER_FULLNAME);
        when(adMapper.toDetailResponse(ad, SELLER_FULLNAME)).thenReturn(expectedResponse);

        AdDetailResponse response = adService.updateAd(adId, SELLER_LOGIN, request);

        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        assertEquals(SELLER_FULLNAME, response.getSellerName());
        verify(adMapper).updateEntity(request, ad);
        verify(adRepository).save(ad);
    }

    @Test
    void updateAd_shouldThrowWhenNoDataToUpdate() {
        AdUpdateRequest request = new AdUpdateRequest(); // все поля null
        assertThrows(IllegalArgumentException.class,
                () -> adService.updateAd(adId, SELLER_LOGIN, request));
        verify(adRepository, never()).findById(any());
    }

    @Test
    void updateAd_shouldThrowWhenAdNotFound() {
        AdUpdateRequest request = new AdUpdateRequest();
        request.setTitle("New");
        when(adRepository.findById(adId)).thenReturn(Optional.empty());
        assertThrows(AdNotFoundException.class,
                () -> adService.updateAd(adId, SELLER_LOGIN, request));
    }

    @Test
    void updateAd_shouldThrowWhenNotOwner() {
        AdUpdateRequest request = new AdUpdateRequest();
        request.setTitle("New");
        UserInternal otherUser = new UserInternal();
        otherUser.setId(UUID.randomUUID());
        otherUser.setLogin("other");
        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(otherUser);
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        assertThrows(NotOwnerException.class,
                () -> adService.updateAd(adId, SELLER_LOGIN, request));
    }

    @Test
    void deleteAd_shouldArchiveAd() {
        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(seller);
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));

        adService.deleteAd(adId, SELLER_LOGIN);

        assertEquals(AdStatus.ARCHIVED, ad.getStatus());
        verify(adRepository).save(ad);
    }

    @Test
    void deleteAd_shouldThrowWhenNotOwner() {
        UserInternal otherUser = new UserInternal();
        otherUser.setId(UUID.randomUUID());
        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(otherUser);
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        assertThrows(NotOwnerException.class,
                () -> adService.deleteAd(adId, SELLER_LOGIN));
        verify(adRepository, never()).save(any());
    }

    @Test
    void getMyAds_shouldReturnList() {
        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(seller);
        when(adRepository.findBySellerId(sellerId)).thenReturn(List.of(ad));

        AdDetailResponse detail = new AdDetailResponse();
        detail.setId(adId);
        detail.setSellerName(SELLER_FULLNAME);
        when(adMapper.toDetailResponse(ad, SELLER_FULLNAME)).thenReturn(detail);

        List<AdDetailResponse> result = adService.getMyAds(SELLER_LOGIN);

        assertEquals(1, result.size());
        assertEquals(adId, result.get(0).getId());
        assertEquals(SELLER_FULLNAME, result.get(0).getSellerName());
    }

    @Test
    void searchAds_shouldReturnListWithSellerNames() {
        AdFilterRequest filter = new AdFilterRequest();
        when(adRepository.searchAds(filter)).thenReturn(List.of(ad));

        when(userServiceClient.getByIds(Set.of(sellerId))).thenReturn(List.of(seller));

        AdResponse adResponse = new AdResponse();
        adResponse.setId(adId);
        adResponse.setSellerName(SELLER_FULLNAME);
        when(adMapper.toResponse(ad, SELLER_FULLNAME)).thenReturn(adResponse);

        List<AdResponse> result = adService.searchAds(filter);

        assertEquals(1, result.size());
        assertEquals(SELLER_FULLNAME, result.get(0).getSellerName());
        verify(userServiceClient).getByIds(Set.of(sellerId));
    }

    @Test
    void getAdsBySeller_shouldReturnActiveAds() {
        when(userServiceClient.getByLogin(SELLER_LOGIN)).thenReturn(seller);
        when(adRepository.findBySellerIdAndStatus(sellerId, AdStatus.ACTIVE)).thenReturn(List.of(ad));

        AdResponse adResponse = new AdResponse();
        adResponse.setId(adId);
        adResponse.setSellerName(SELLER_FULLNAME);
        when(adMapper.toResponse(ad, SELLER_FULLNAME)).thenReturn(adResponse);

        List<AdResponse> result = adService.getAdsBySeller(SELLER_LOGIN);

        assertEquals(1, result.size());
        assertEquals(SELLER_FULLNAME, result.get(0).getSellerName());
    }

    @Test
    void promoteAd_shouldSetPromotedUntilFromNow() {
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        LocalDateTime before = LocalDateTime.now();
        adService.promoteAd(adId, 5);
        LocalDateTime after = ad.getPromotedUntil();
        assertNotNull(after);
        // приблизительная проверка, что +5 часов
        assertEquals(before.plusHours(5).getHour(), after.getHour());
        verify(adRepository).save(ad);
    }

    @Test
    void promoteAd_shouldExtendExistingPromotion() {
        LocalDateTime existing = LocalDateTime.now().plusHours(2);
        ad.setPromotedUntil(existing);
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        adService.promoteAd(adId, 3);
        assertEquals(existing.plusHours(3), ad.getPromotedUntil());
        verify(adRepository).save(ad);
    }

    @Test
    void blockAd_shouldSetArchived() {
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        adService.blockAd(adId);
        assertEquals(AdStatus.ARCHIVED, ad.getStatus());
        verify(adRepository).save(ad);
    }

    @Test
    void unblockAd_shouldSetActive() {
        ad.setStatus(AdStatus.ARCHIVED);
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        adService.unblockAd(adId);
        assertEquals(AdStatus.ACTIVE, ad.getStatus());
        verify(adRepository).save(ad);
    }

    @Test
    void getAdById_shouldReturnInternal() {
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        AdInternal internal = new AdInternal();
        internal.setId(adId);
        when(adMapper.toInternal(ad)).thenReturn(internal);

        AdInternal result = adService.getAdById(adId);
        assertEquals(adId, result.getId());
    }

    @Test
    void getAdById_shouldThrowWhenNotFound() {
        when(adRepository.findById(adId)).thenReturn(Optional.empty());
        assertThrows(AdNotFoundException.class, () -> adService.getAdById(adId));
    }
}