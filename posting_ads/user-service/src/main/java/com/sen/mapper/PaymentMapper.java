package com.sen.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sen.dto.response.PaymentResponse;
import com.sen.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentResponse toResponse(Payment payment);

    // @Mapping(target = "id", ignore = true)
    // @Mapping(target = "user", ignore = true)
    // @Mapping(target = "status", ignore = true) 
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "processedAt", ignore = true)
    // Payment toEntity(PaymentCreateRequest request);
}
