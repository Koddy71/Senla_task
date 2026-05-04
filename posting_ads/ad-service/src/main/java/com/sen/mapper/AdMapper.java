package com.sen.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.sen.dto.request.AdCreateRequest;
import com.sen.dto.request.AdUpdateRequest;
import com.sen.dto.response.AdDetailResponse;
import com.sen.dto.response.AdResponse;
import com.sen.entity.Ad;

@Mapper(componentModel = "spring")
public interface AdMapper {
    AdMapper INSTANCE = Mappers.getMapper(AdMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sellerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "promotedUntil", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Ad toEntity(AdCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sellerId", ignore = true)
    @Mapping(target = "promotedUntil", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(AdUpdateRequest request, @MappingTarget Ad ad);

    @Mapping(target = "sellerName", source = "sellerName")
    @Mapping(target = "promoted", expression = "java(ad.getPromotedUntil() != null && ad.getPromotedUntil().isAfter(java.time.LocalDateTime.now()))")
    AdResponse toResponse(Ad ad, String sellerName);

    @Mapping(target = "sellerName", source = "sellerName")
    @Mapping(target = "promoted", expression = "java(ad.getPromotedUntil() != null && ad.getPromotedUntil().isAfter(java.time.LocalDateTime.now()))")
    AdDetailResponse toDetailResponse(Ad ad, String sellerName);
}
