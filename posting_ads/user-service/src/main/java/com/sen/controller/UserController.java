package com.sen.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sen.dto.request.BalanceUpRequest;
import com.sen.dto.request.LoginRequest;
import com.sen.dto.request.RegistrationRequest;
import com.sen.dto.request.UserFilterRequest;
import com.sen.dto.request.UserUpdateRequest;
import com.sen.dto.response.PrivateUserResponse;
import com.sen.dto.response.PublicUserResponse;
import com.sen.dto.response.TokenResponse;
import com.sen.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<PrivateUserResponse> register(@Valid @RequestBody RegistrationRequest request) {
        logger.info("Входящий запрос на регистрацию пользователя с логином: {}", request.getLogin());
        PrivateUserResponse response = userService.register(request);
        logger.info("Пользователь успешно зарегистрирован: {}", response.getLogin());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Входящий запрос на аутентификацию пользователя: {}", request.getLogin());
        TokenResponse response = userService.login(request);
        logger.info("Пользователь успешно аутентифицирован: {}", request.getLogin());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{login}")
    public ResponseEntity<PublicUserResponse> getPublicProfile(@PathVariable String login) {
        logger.info("Запрос публичного профиля пользователя: {}", login);
        PublicUserResponse response = userService.getPublicProfile(login);
        logger.info("Публичный профиль пользователя {} успешно получен", login);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<PrivateUserResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Запрос собственного профиля текущим пользователем: {}", userDetails.getUsername());
        PrivateUserResponse response = userService.getMyProfile(userDetails.getUsername());
        logger.info("Собственный профиль успешно получен для пользователя: {}", response.getLogin());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/my")
    public ResponseEntity<PrivateUserResponse> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest request) {
        logger.info("Запрос на обновление собственного профиля пользователя: {}", userDetails.getUsername());
        PrivateUserResponse response = userService.updateMyProfile(userDetails.getUsername(), request);
        logger.info("Собственный профиль успешно обновлён для пользователя: {}", response.getLogin());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/my/balance")
    public ResponseEntity<PrivateUserResponse> upBalance(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BalanceUpRequest request) {
        logger.info("Запрос на пополнение баланса пользователем {} на сумму: {}", userDetails.getUsername(),
                request.getAmount());
        PrivateUserResponse response = userService.balanceUp(userDetails.getUsername(), request);
        logger.info("Баланс успешно пополнен. Текущий баланс: {}", response.getBalance());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/my")
    public ResponseEntity<Void> deleteMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Запрос на удаление профиля пользователя: {}", userDetails.getUsername());
        userService.deleteMyProfile(userDetails.getUsername());
        logger.info("Профиль пользователя {} успешно удалён", userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/{login}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<PrivateUserResponse> getFullProfile(@PathVariable String login) {
        logger.info("Административный запрос полного профиля пользователя: {}", login);
        PrivateUserResponse response = userService.getFullProfile(login);
        logger.info("Полный профиль пользователя {} успешно получен", login);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<PrivateUserResponse>> getAllUsers(UserFilterRequest filter) {
        logger.info("Административный запрос списка всех пользователей с фильтром: {}", filter);
        List<PrivateUserResponse> response = userService.getAllUsers(filter);
        logger.info("Список всех пользователей успешно получен, кол-во: {}", response.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/{login}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeRole(@PathVariable String login,
            @RequestParam String role) {
        logger.info("Административный запрос на изменение роли пользователя {} на {}", login, role);
        userService.changeUserRole(login, role);
        logger.info("Роль пользователя {} успешно изменена на {}", login, role);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/{login}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockUser(@PathVariable String login) {
        logger.info("Административный запрос на блокировку пользователя: {}", login);
        userService.blockUser(login);
        logger.info("Пользователь {} успешно заблокирован", login);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/{login}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unblockUser(@PathVariable String login) {
        logger.info("Административный запрос на разблокировку пользователя: {}", login);
        userService.unblockUser(login);
        logger.info("Пользователь {} успешно разблокирован", login);
        return ResponseEntity.noContent().build();
    }
}