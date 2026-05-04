package com.sen.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdServiceImpl implements AdService{
    private final AdRepository adRepository;
    private final UserServiceClient userServiceClient;
    private final AdMapper adMapper;

    public AdServiceImpl(AdRepository adRepository, UserServiceClient userServiceClient, AdMapper adMapper) {
        this.adRepository = adRepository;
        this.userServiceClient = userServiceClient;
        this.adMapper=adMapper;
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
        Ad ad =  adRepository.findById(adId)
            .orElseThrow(() -> new AdNotFoundException(adId));
        UserInternalResponse user = userServiceClient.getByLogin(myLogin);
        if (!ad.getSellerId().equals(user.getId())){
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchAds'");
    }

    @Override
    public List<AdResponse> getAdsBySeller(String sellerLogin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAdsBySeller'");
    }

    @Override
    public void promoteAd(UUID adId, int hours) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'promoteAd'");
    }

    @Override
    public void blockAds(String sellerLogin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'blockAds'");
    }

    @Override
    public void unblockAds(String sellerLogin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unblockAds'");
    }
}
