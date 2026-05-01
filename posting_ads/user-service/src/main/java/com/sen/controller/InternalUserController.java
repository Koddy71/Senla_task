package com.sen.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sen.dto.internal.UserInternal;
import com.sen.service.UserService;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {
    private static final Logger logger = LoggerFactory.getLogger(InternalUserController.class);
    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserInternal> getInternalUser(@PathVariable String login) {
        logger.debug("Внутренний запрос на получение пользователя: {}", login);
        UserInternal user = userService.getInternalUser(login);
        logger.debug("Внутренний запрос выполнен успешно для пользователя: {}", login);
        return ResponseEntity.ok(user);
    }
}