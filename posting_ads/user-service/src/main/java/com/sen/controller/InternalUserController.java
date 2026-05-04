package com.sen.controller;

import java.util.UUID;

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
@RequestMapping("/internal/users")//todo: опиши потом в чэм разница двух контроллеров для этого и просто usercontroller
public class InternalUserController {
    private static final Logger logger = LoggerFactory.getLogger(InternalUserController.class);
    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserInternal> getInternalUserByLogin(@PathVariable String login) {
        logger.debug("Внутренний запрос на получение пользователя: {}", login);
        UserInternal user = userService.getInternalUserByLogin(login);
        logger.debug("Внутренний запрос выполнен успешно для пользователя: {}", login);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInternal> getInternalUserById(@PathVariable UUID id) {
        logger.debug("Внутренний запрос на получение пользователя: {}", id);
        UserInternal user = userService.getInternalUserById(id);
        logger.debug("Внутренний запрос выполнен успешно для пользователя: {}", id);
        return ResponseEntity.ok(user);
    }
}