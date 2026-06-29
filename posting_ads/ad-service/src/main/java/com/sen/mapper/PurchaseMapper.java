package com.sen.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sen.dto.response.PurchaseResponse;
import com.sen.entity.Purchase;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    @Mapping(source = "purchase.ad.id", target = "adId")
    PurchaseResponse toResponse(Purchase purchase, String buyerLogin);
}
