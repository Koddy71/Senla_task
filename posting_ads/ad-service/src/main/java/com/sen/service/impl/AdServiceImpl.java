package com.sen.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sen.client.UserServiceClient;
import com.sen.dto.internal.UserInternalResponse;
import com.sen.dto.request.AdCreateRequest;
import com.sen.dto.request.AdFilterRequest;
import com.sen.dto.request.AdUpdateRequest;
import com.sen.dto.response.AdDetailResponse;
import com.sen.dto.response.AdResponse;
import com.sen.entity.Ad;
import com.sen.enums.AdStatus;
import com.sen.exception.AdNotFoundException;
import com.sen.exception.NotOwnerException;
import com.sen.exception.UserIsBlockedException;
import com.sen.mapper.AdMapper;
import com.sen.repository.AdRepository;
import com.sen.service.AdService;

@Service
@Transactional
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final UserServiceClient userServiceClient;
    private final AdMapper adMapper;

    public AdServiceImpl(AdRepository adRepository, UserServiceClient userServiceClient, AdMapper adMapper) {
        this.adRepository = adRepository;
        this.userServiceClient = userServiceClient;
        this.adMapper = adMapper;
    }

    @Override
    public AdDetailResponse createAd(String myLogin, AdCreateRequest request) {
        UserInternalResponse user = userServiceClient.getByLogin(myLogin);
        if (user.isBlocked()) {
            throw new UserIsBlockedException(myLogin);
        }

        Ad ad = adMapper.toEntity(request);
        ad.setSellerId(user.getId());
        ad.setStatus(AdStatus.ACTIVE);
        ad.setPromotedUntil(null);

        Ad saved = adRepository.save(ad);
        return adMapper.toDetailResponse(saved, user.getFullName());
    }

    @Override
    public AdDetailResponse updateAd(UUID adId, String myLogin, AdUpdateRequest request) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException(adId));
        UserInternalResponse user = userServiceClient.getByLogin(myLogin);
        if (!ad.getSellerId().equals(user.getId())) {
            throw new NotOwnerException();
        }

        adMapper.updateEntity(request, ad);
        return adMapper.toDetailResponse(adRepository.save(ad), user.getFullName());
    }

    @Override
    public void deleteAd(UUID adId, String myLogin) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException(adId));

        UserInternalResponse user = userServiceClient.getByLogin(myLogin);
        if (!ad.getSellerId().equals(user.getId())) {
            throw new NotOwnerException();
        }

        ad.setStatus(AdStatus.ARCHIVED);
        adRepository.save(ad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdDetailResponse> getMyAds(String myLogin) {
        UserInternalResponse user = userServiceClient.getByLogin(myLogin);
        String userFullName = user.getFullName();
        return adRepository.findBySellerId(user.getId()).stream()
                .map(ad -> adMapper.toDetailResponse(ad, userFullName))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdResponse> searchAds(AdFilterRequest filter) {
        List<Ad> ads = adRepository.searchAds(filter);
        Map<UUID, String> sellerNames = resolveSellerNames(ads);

        return ads.stream()
                .map(ad -> {
                    String sellerName = sellerNames.getOrDefault(ad.getSellerId(), "Неизвестно");
                    AdResponse resp = adMapper.toResponse(ad, sellerName);
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AdResponse> getAdsBySeller(String sellerLogin) {
        UserInternalResponse user =userServiceClient.getByLogin(sellerLogin);
        String userFullName = user.getFullName();
        List<Ad> ads = adRepository.findBySellerIdAndStatus(user.getId(), AdStatus.ACTIVE);

        return ads.stream()
                .map(ad -> adMapper.toResponse(ad, userFullName))
                .collect(Collectors.toList());
    }

    @Override
    public void promoteAd(UUID adId, int hours) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException(adId));
        
        LocalDateTime base;
        boolean isPromotedActive = ad.getPromotedUntil().isAfter(LocalDateTime.now());        
        if(ad.getPromotedUntil()!=null && isPromotedActive){
            base = ad.getPromotedUntil();
        } else {
            base = LocalDateTime.now();
        }

        ad.setPromotedUntil(base.plusHours(hours));
        adRepository.save(ad);
    }

    @Override
    public void blockAd(UUID adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException(adId));
        ad.setStatus(AdStatus.ARCHIVED);
        adRepository.save(ad);
    }

    @Override
    public void unblockAd(UUID adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException(adId));
        ad.setStatus(AdStatus.ACTIVE);
        adRepository.save(ad);
    }

    private Map<UUID, String> resolveSellerNames(List<Ad> ads) {
        Set<UUID> sellersIds = ads.stream()
                .map(Ad::getSellerId)
                .collect(Collectors.toSet());
        List<UserInternalResponse> users = userServiceClient.getByIds(sellersIds);
        return users.stream()
                .collect(Collectors.toMap(UserInternalResponse::getId, UserInternalResponse::getFullName));
    }
}
