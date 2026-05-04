package com.sen.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sen.dto.internal.UserInternal;
import com.sen.dto.request.BalanceUpRequest;
import com.sen.dto.request.LoginRequest;
import com.sen.dto.request.RegistrationRequest;
import com.sen.dto.request.UserFilterRequest;
import com.sen.dto.request.UserUpdateRequest;
import com.sen.dto.response.TokenResponse;
import com.sen.dto.response.PrivateUserResponse;
import com.sen.dto.response.PublicUserResponse;
import com.sen.entity.User;
import com.sen.enums.Role;
import com.sen.exception.UserAlreadyExistsException;
import com.sen.exception.UserNotFoundException;
import com.sen.mapper.UserMapper;
import com.sen.repository.UserRepository;
import com.sen.security.JwtTokenProvider;
import com.sen.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userMapper = userMapper;
    }

    @Override
    public PrivateUserResponse register(RegistrationRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            logger.error("Ошибка регистрации. Логин {} уже существует", request.getLogin());
            throw new UserAlreadyExistsException("Login already exists: " + request.getLogin());
        }

        User user = userMapper.toEntity(request);

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setBalance(new java.math.BigDecimal("0.00"));
        user.setBlocked(false);
        if (request.getRole() != Role.USER) {
            user.setRole(Role.USER);
        }

        User saved = userRepository.save(user);
        logger.info("Пользователь {} успешно зарегистрирован с ролью {}", saved.getLogin(), saved.getRole());
        return userMapper.toPrivateUserResponse(saved);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            String token = tokenProvider.generateToken(authentication);
            logger.info("Пользователь {} успешно аутентифицирован", request.getLogin());
            return new TokenResponse(token);
        } catch (BadCredentialsException e) {
            logger.error("Ошибка аутентификации пользователя {}: неверный логин или пароль", request.getLogin());
            throw new BadCredentialsException("Invalid login or password");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PublicUserResponse getPublicProfile(String login) {
        logger.info("Запрос публичного профиля пользователя {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден", login);
                    return new UserNotFoundException("User not found exception: " + login);
                });
        logger.info("Публичный профиль пользователя {} успешно получен", login);
        return userMapper.toPublicUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PrivateUserResponse getMyProfile(String login) {
        logger.info("Запрос собственного профиля пользователя {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден в базе", login);
                    return new UserNotFoundException();
                });
        logger.info("Собственный профиль пользователя {} успешно получен", login);
        return userMapper.toPrivateUserResponse(user);
    }

    @Override
    public PrivateUserResponse updateMyProfile(String login, UserUpdateRequest request) {
        logger.info("Запрос на обновление профиля пользователя {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден при обновлении профиля", login);
                    return new UserNotFoundException();
                });
        userMapper.updateEntity(request, user);
        User updated = userRepository.save(user);
        logger.info("Профиль пользователя {} успешно обновлён", login);
        return userMapper.toPrivateUserResponse(updated);
    }

    @Override
    public void deleteMyProfile(String login) {
        logger.info("Удаление профиля пользователя {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден при удалении профиля", login);
                    return new UserNotFoundException();
                });
        user.setBlocked(true);
        userRepository.save(user);
        logger.info("Профиль пользователя {} успешно удалён", login);
    }

    @Override
    @Transactional(readOnly = true)
    public PrivateUserResponse balanceUp(String login, BalanceUpRequest request) {
        logger.info("Запрос на пополнение баланса для пользователя {} на сумму {}", login, request.getAmount());
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден при пополнении баланса", login);
                    return new UserNotFoundException();
                });
        user.setBalance(user.getBalance().add(request.getAmount()));
        User updated = userRepository.save(user);
        logger.info("Баланс пользователя {} успешно пополнен. Новый баланс: {}", login, updated.getBalance());
        return userMapper.toPrivateUserResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PrivateUserResponse getFullProfile(String login) {
        logger.info("Административный запрос полного профиля пользователя {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден для получения полного профиля", login);
                    return new UserNotFoundException("User not found: " + login);
                });
        logger.info("Полный профиль пользователя {} успешно получен", login);
        return userMapper.toPrivateUserResponse(user);
    }

    @Override
    public List<PrivateUserResponse> getAllUsers(UserFilterRequest filter) {
        logger.info("Запрос списка всех пользователей с фильтром: страница {}, размер {}", filter.getPage(),
                filter.getSize());
        List<User> users = userRepository.findAll(filter.getPage(), filter.getSize());
        List<PrivateUserResponse> response = users.stream()
                .map(userMapper::toPrivateUserResponse)
                .collect(Collectors.toList());
        logger.info("Список всех пользователей успешно получен, найдено записей: {}", response.size());
        return response;
    }

    @Override
    public void changeUserRole(String login, String newRole) {
        logger.info("Запрос на изменение роли пользователя {} на {}", login, newRole);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден при изменении роли", login);
                    return new UserNotFoundException("User not found: " + login);
                });
        user.setRole(Role.valueOf(newRole));
        userRepository.save(user);
        logger.info("Роль пользователя {} успешно изменена на {}", login, newRole);
    }

    @Override
    public void blockUser(String login) {
        logger.info("Запрос на блокировку пользователя {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден при блокировке", login);
                    return new UserNotFoundException("User not found: " + login);
                });
        user.setBlocked(true);
        userRepository.save(user);
        logger.info("Пользователь {} успешно заблокирован", login);
    }

    @Override
    public void unblockUser(String login) {
        logger.info("Запрос на разблокировку пользователя {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден при разблокировке", login);
                    return new UserNotFoundException("User not found: " + login);
                });
        user.setBlocked(false);
        userRepository.save(user);
        logger.info("Пользователь {} успешно разблокирован", login);
    }

    @Override
    public UserInternal getInternalUserByLogin(String login) {
        logger.debug("Внутренний запрос на получение пользователя {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден для внутреннего запроса", login);
                    return new UserNotFoundException("User not found: " + login);
                });
        logger.debug("Внутренний запрос для пользователя {} выполнен успешно", login);
        return userMapper.toInternal(user);
    }

    @Override
    public UserInternal getInternalUserById(UUID id) {
        logger.debug("Внутренний запрос на получение пользователя {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден для внутреннего запроса", id);
                    return new UserNotFoundException("User not found: " + id);
                });
        logger.debug("Внутренний запрос для пользователя {} выполнен успешно", id);
        return userMapper.toInternal(user);
    }
}