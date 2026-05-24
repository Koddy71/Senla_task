package com.sen.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sen.entity.Dialog;

public interface DialogRepository {
    Optional<Dialog> findById(UUID id);

    Optional<Dialog> findByUserId(UUID userId);

    List<Dialog> findDialogsByUserId(UUID userId);

    Dialog save(Dialog dialog);

    void deleteById(UUID id);
}