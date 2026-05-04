package com.sen.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.sen.dto.response.PurchaseResponse;
import com.sen.entity.Purchase;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    PurchaseMapper INSTANCE = Mappers.getMapper(PurchaseMapper.class);//todo: скорее не нужно

    @Mapping(source = "ad.id", target = "adId")
    PurchaseResponse toResponse(Purchase purchase);
}
