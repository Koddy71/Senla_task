package com.sen.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.sen.service.AdService;

@Service
@Transactional
public class AdServiceImpl implements AdService {

    private static final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);

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
        logger.info("Запрос на создание объявления от пользователя {}", myLogin);
        UserInternal user = getUserByLogin(myLogin);
        if (user.isBlocked()) {
            logger.error("Пользователь {} заблокирован, создание объявления невозможно", myLogin);
            throw new UserBlockedException(myLogin);
        }

        Ad ad = adMapper.toEntity(request);
        ad.setSellerId(user.getId());
        ad.setStatus(AdStatus.ACTIVE);
        ad.setPromotedUntil(null);

        Ad saved = adRepository.save(ad);
        logger.info("Объявление успешно создано, id: {}, sellerId: {}", saved.getId(), saved.getSellerId());
        return adMapper.toDetailResponse(saved, user.getFullname());
    }

    @Override
    public AdDetailResponse updateAd(UUID adId, String myLogin, AdUpdateRequest request) {
        logger.info("Запрос на обновление объявления {} от пользователя {}", adId, myLogin);
        Ad ad = findAdById(adId);
        UserInternal user = getUserByLogin(myLogin);
        checkOwnership(ad, user);

        adMapper.updateEntity(request, ad);
        Ad updated = adRepository.save(ad);
        logger.info("Объявление {} успешно обновлено", adId);
        return adMapper.toDetailResponse(updated, user.getFullname());
    }

    @Override
    public void deleteAd(UUID adId, String myLogin) {
        logger.info("Запрос на удаление (архивацию) объявления {} от пользователя {}", adId, myLogin);
        Ad ad = findAdById(adId);
        UserInternal user = getUserByLogin(myLogin);
        checkOwnership(ad, user);

        ad.setStatus(AdStatus.ARCHIVED);
        adRepository.save(ad);
        logger.info("Объявление {} успешно архивировано", adId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdDetailResponse> getMyAds(String myLogin) {
        logger.info("Запрос списка объявлений пользователя {}", myLogin);
        UserInternal user = getUserByLogin(myLogin);
        String userFullName = user.getFullname();
        List<AdDetailResponse> ads = adRepository.findBySellerId(user.getId()).stream()
                .map(ad -> adMapper.toDetailResponse(ad, userFullName))
                .collect(Collectors.toList());
        logger.info("Найдено {} объявлений для пользователя {}", ads.size(), myLogin);
        return ads;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdResponse> searchAds(AdFilterRequest filter) {
        logger.info("Поиск объявлений с фильтром: {}", filter);
        List<Ad> ads = adRepository.searchAds(filter);
        Map<UUID, String> sellerNames = resolveSellerNames(ads);
        List<AdResponse> responses = ads.stream()
                .map(ad -> {
                    String sellerName = sellerNames.getOrDefault(ad.getSellerId(), "Неизвестно");
                    return adMapper.toResponse(ad, sellerName);
                })
                .collect(Collectors.toList());
        logger.info("Поиск завершён, найдено {} объявлений", responses.size());
        return responses;
    }

    @Override
    public List<AdResponse> getAdsBySeller(String sellerLogin) {
        logger.info("Запрос активных объявлений продавца {}", sellerLogin);
        UserInternal user = getUserByLogin(sellerLogin);
        String userFullName = user.getFullname();
        List<Ad> ads = adRepository.findBySellerIdAndStatus(user.getId(), AdStatus.ACTIVE);
        List<AdResponse> responses = ads.stream()
                .map(ad -> adMapper.toResponse(ad, userFullName))
                .collect(Collectors.toList());
        logger.info("Найдено {} активных объявлений у продавца {}", responses.size(), sellerLogin);
        return responses;
    }

    @Override
    public void promoteAd(UUID adId, int hours) {
        logger.info("Запрос на продвижение объявления {} на {} часов", adId, hours);
        Ad ad = findAdById(adId);

        LocalDateTime base;
        if (ad.getPromotedUntil() != null && ad.getPromotedUntil().isAfter(LocalDateTime.now())) {
            base = ad.getPromotedUntil();
            logger.debug("Продвижение уже активно, продлеваем с {}", base);
        } else {
            base = LocalDateTime.now();
            logger.debug("Продвижение не активно, начинаем с текущего момента");
        }

        ad.setPromotedUntil(base.plusHours(hours));
        adRepository.save(ad);
        logger.info("Объявление {} будет продвигаться до {}", adId, ad.getPromotedUntil());
    }

    @Override
    public void blockAd(UUID adId) {
        logger.info("Запрос на блокировку объявления {}", adId);
        Ad ad = findAdById(adId);
        ad.setStatus(AdStatus.ARCHIVED);
        adRepository.save(ad);
        logger.info("Объявление {} заблокировано (статус ARCHIVED)", adId);
    }

    @Override
    public void unblockAd(UUID adId) {
        logger.info("Запрос на разблокировку объявления {}", adId);
        Ad ad = findAdById(adId);
        ad.setStatus(AdStatus.ACTIVE);
        adRepository.save(ad);
        logger.info("Объявление {} разблокировано (статус ACTIVE)", adId);
    }


    @Override
    public AdInternal getAdById(UUID id) {
        logger.debug("Запрос внутреннего представления объявления {}", id);
        Ad ad = findAdById(id);
        AdInternal internal = adMapper.toInternal(ad);
        logger.debug("Внутреннее представление объявления {} получено", id);
        return internal;
    }

    private Map<UUID, String> resolveSellerNames(List<Ad> ads) {
        Set<UUID> sellersIds = ads.stream()
                .map(Ad::getSellerId)
                .collect(Collectors.toSet());
        List<UserInternal> users = userServiceClient.getByIds(sellersIds);
        return users.stream()
                .collect(Collectors.toMap(UserInternal::getId, UserInternal::getFullname));
    }

    private Ad findAdById(UUID adId) {
        return adRepository.findById(adId)
                .orElseThrow(() -> {
                    logger.error("Объявление с id {} не найдено", adId);
                    return new AdNotFoundException(adId);
                });
    }

    private UserInternal getUserByLogin(String login) {
        return userServiceClient.getByLogin(login);
    }

    private void checkOwnership(Ad ad, UserInternal user) {
        if (!ad.getSellerId().equals(user.getId())) {
            logger.error("Пользователь {} не является владельцем объявления {}", user.getLogin(), ad.getId());
            throw new NotOwnerException();
        }
    }
}