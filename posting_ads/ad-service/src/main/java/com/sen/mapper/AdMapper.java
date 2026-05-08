package com.sen.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sen.dto.internal.AdInternal;
import com.sen.dto.request.AdCreateRequest;
import com.sen.dto.request.AdUpdateRequest;
import com.sen.dto.response.AdDetailResponse;
import com.sen.dto.response.AdResponse;
import com.sen.entity.Ad;

@Mapper(componentModel = "spring")
public interface AdMapper {
    default boolean isPromoted(Ad ad) {
        return ad.getPromotedUntil() != null &&
                ad.getPromotedUntil().isAfter(java.time.LocalDateTime.now());
    }

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

    @Mapping(target = "promoted", expression = "java(isPromoted(ad))")
    AdResponse toResponse(Ad ad, String sellerName);

    @Mapping(target = "promoted", expression = "java(isPromoted(ad))")
    AdDetailResponse toDetailResponse(Ad ad, String sellerName);

    @Mapping(target = "status", expression = "java(ad.getStatus().name())")
    AdInternal toInternal(Ad ad);
}
