package com.sen.mapper;

import org.mapstruct.Mapper;

import com.sen.dto.response.MessageResponse;
import com.sen.entity.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse toMessageResponse(Message message, String senderLogin);
}