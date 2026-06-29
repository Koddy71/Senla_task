package com.sen.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sen.entity.Message;

public interface MessageRepository {
    Optional<Message> findById(UUID id);

    List<Message> findByDialogId(UUID dialogId);

    Message save(Message message);

    boolean deleteById(UUID id);
}