package com.sen.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sen.entity.User;

public interface UserRepository {
    Optional<User> findById(UUID id);
    
    Optional<User> findByLogin(String login);

    List<User> findAll(int page, int size);

    User save(User user);
    
    void delete(User user);

    boolean existsByLogin(String login);
}
