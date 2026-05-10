package com.sen.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sen.dto.internal.UserInternal;
import com.sen.dto.request.RegistrationRequest;
import com.sen.dto.request.UserUpdateRequest;
import com.sen.dto.response.PrivateUserResponse;
import com.sen.dto.response.PublicUserResponse;
import com.sen.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    PrivateUserResponse toPrivateUserResponse(User user);

    PublicUserResponse toPublicUserResponse(User user);

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserInternal toInternal(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) 
    @Mapping(target = "balance", ignore = true) 
    @Mapping(target = "blocked", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegistrationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "login", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UserUpdateRequest request, @MappingTarget User user);
}
