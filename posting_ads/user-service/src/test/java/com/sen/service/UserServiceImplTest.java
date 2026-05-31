package com.sen.service;

import com.sen.dto.internal.UserInternal;
import com.sen.dto.request.BalanceUpRequest;
import com.sen.dto.request.LoginRequest;
import com.sen.dto.request.RegistrationRequest;
import com.sen.dto.request.UserFilterRequest;
import com.sen.dto.request.UserUpdateRequest;
import com.sen.dto.response.PrivateUserResponse;
import com.sen.dto.response.PublicUserResponse;
import com.sen.dto.response.TokenResponse;
import com.sen.entity.User;
import com.sen.enums.Role;
import com.sen.exception.UserAlreadyExistsException;
import com.sen.exception.UserBlockedException;
import com.sen.exception.UserNotFoundException;
import com.sen.mapper.UserMapper;
import com.sen.repository.UserRepository;
import com.sen.security.JwtTokenProvider;
import com.sen.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID testId;
    private static final String TEST_LOGIN = "testuser";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testId);
        testUser.setLogin(TEST_LOGIN);
        testUser.setPasswordHash("encodedPass");
        testUser.setFullname("Test User");
        testUser.setPhone("+79999999999");
        testUser.setBalance(new BigDecimal("100.00"));
        testUser.setRole(Role.USER);
        testUser.setBlocked(false);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    //  POSITIVE TESTS 

    @Test
    void register_shouldCreateNewUser() {
        RegistrationRequest req = new RegistrationRequest();
        req.setLogin("newuser");
        req.setPassword("password123");
        req.setFullname("New User");

        when(userRepository.existsByLogin("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        User newUser = new User();
        newUser.setLogin("newuser");
        newUser.setFullname("New User");
        when(userMapper.toEntity(any(RegistrationRequest.class))).thenReturn(newUser);

        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        PrivateUserResponse expectedResponse = new PrivateUserResponse();
        expectedResponse.setLogin("newuser");
        expectedResponse.setFullname("New User");
        expectedResponse.setRole(Role.USER);
        when(userMapper.toPrivateUserResponse(any(User.class))).thenReturn(expectedResponse);

        PrivateUserResponse response = userService.register(req);

        assertNotNull(response);
        assertEquals("newuser", response.getLogin());
        assertEquals("New User", response.getFullname());
        assertEquals(Role.USER, response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_shouldReturnToken() {
        LoginRequest req = new LoginRequest();
        req.setLogin(TEST_LOGIN);
        req.setPassword(TEST_PASSWORD);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(tokenProvider.generateToken(auth)).thenReturn("jwt.token.here");

        TokenResponse response = userService.login(req);

        assertNotNull(response);
        assertEquals("jwt.token.here", response.getToken());
    }

    @Test
    void getPublicProfile_shouldReturnPublicData() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        PublicUserResponse publicResponse = new PublicUserResponse();
        publicResponse.setLogin(TEST_LOGIN);
        when(userMapper.toPublicUserResponse(testUser)).thenReturn(publicResponse);

        PublicUserResponse response = userService.getPublicProfile(TEST_LOGIN);

        assertNotNull(response);
        assertEquals(TEST_LOGIN, response.getLogin());
    }

    @Test
    void getMyProfile_shouldReturnPrivateData() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        PrivateUserResponse privateResponse = new PrivateUserResponse();
        privateResponse.setId(testId);
        privateResponse.setBalance(new BigDecimal("100.00"));
        when(userMapper.toPrivateUserResponse(testUser)).thenReturn(privateResponse);

        PrivateUserResponse response = userService.getMyProfile(TEST_LOGIN);

        assertNotNull(response);
        assertEquals(testId, response.getId());
        assertEquals(new BigDecimal("100.00"), response.getBalance());
    }

    @Test
    void updateMyProfile_shouldUpdateFields() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(userMapper).updateEntity(any(UserUpdateRequest.class), any(User.class));

        PrivateUserResponse privateResponse = new PrivateUserResponse();
        privateResponse.setFullname("Updated Name");
        privateResponse.setPhone("+78888888888");
        when(userMapper.toPrivateUserResponse(testUser)).thenReturn(privateResponse);

        UserUpdateRequest req = new UserUpdateRequest();
        req.setFullname("Updated Name");
        req.setPhone("+78888888888");

        PrivateUserResponse response = userService.updateMyProfile(TEST_LOGIN, req);

        assertEquals("Updated Name", response.getFullname());
        assertEquals("+78888888888", response.getPhone());
        verify(userMapper).updateEntity(any(UserUpdateRequest.class), any(User.class));
    }

    @Test
    void deleteMyProfile_shouldSetBlockedTrue() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deleteMyProfile(TEST_LOGIN);

        assertTrue(testUser.getBlocked());
        verify(userRepository).save(testUser);
    }

    @Test
    void balanceUp_shouldAddToBalance() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        PrivateUserResponse privateResponse = new PrivateUserResponse();
        privateResponse.setBalance(new BigDecimal("150.00"));
        when(userMapper.toPrivateUserResponse(testUser)).thenReturn(privateResponse);

        BalanceUpRequest req = new BalanceUpRequest();
        req.setAmount(new BigDecimal("50.00"));

        PrivateUserResponse response = userService.balanceUp(TEST_LOGIN, req);

        assertEquals(new BigDecimal("150.00"), response.getBalance());
    }

    @Test
    void getFullProfile_shouldReturnFullData() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        PrivateUserResponse privateResponse = new PrivateUserResponse();
        privateResponse.setId(testId);
        privateResponse.setLogin(TEST_LOGIN);
        when(userMapper.toPrivateUserResponse(testUser)).thenReturn(privateResponse);

        PrivateUserResponse response = userService.getFullProfile(TEST_LOGIN);

        assertNotNull(response);
        assertEquals(testId, response.getId());
        assertEquals(TEST_LOGIN, response.getLogin());
    }

    @Test
    void changeUserRole_shouldUpdateRole() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changeUserRole(TEST_LOGIN, "MANAGER");

        assertEquals(Role.MANAGER, testUser.getRole());
    }

    @Test
    void blockUser_shouldSetBlockedTrue() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.blockUser(TEST_LOGIN);

        assertTrue(testUser.getBlocked());
    }

    @Test
    void unblockUser_shouldSetBlockedFalse() {
        testUser.setBlocked(true);
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.unblockUser(TEST_LOGIN);

        assertFalse(testUser.getBlocked());
    }

    @Test
    void getInternalUserByLogin_shouldReturnInternalDto() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        UserInternal internalResponse = new UserInternal();
        internalResponse.setId(testId);
        internalResponse.setLogin(TEST_LOGIN);
        internalResponse.setBlocked(false);
        when(userMapper.toInternal(testUser)).thenReturn(internalResponse);

        UserInternal response = userService.getInternalUserByLogin(TEST_LOGIN);

        assertNotNull(response);
        assertEquals(testId, response.getId());
        assertEquals(TEST_LOGIN, response.getLogin());
        assertFalse(response.getBlocked());
    }

    @Test
    void getInternalUserById_shouldReturnInternalDto() {
        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        UserInternal internalResponse = new UserInternal();
        internalResponse.setId(testId);
        when(userMapper.toInternal(testUser)).thenReturn(internalResponse);

        UserInternal response = userService.getInternalUserById(testId);

        assertNotNull(response);
        assertEquals(testId, response.getId());
    }

    @Test
    void getInternalUsersByIds_shouldReturnList() {
        List<UUID> ids = List.of(testId);
        when(userRepository.findAllById(ids)).thenReturn(List.of(testUser));
        UserInternal dto = new UserInternal();
        when(userMapper.toInternal(testUser)).thenReturn(dto);

        List<UserInternal> result = userService.getInternalUsersByIds(ids);

        assertEquals(1, result.size());
    }

    //NEGATIVE TESTS

    @Test
    void register_shouldThrowWhenLoginExists() {
        RegistrationRequest req = new RegistrationRequest();
        req.setLogin(TEST_LOGIN);
        when(userRepository.existsByLogin(TEST_LOGIN)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldThrowOnBadCredentials() {
        LoginRequest req = new LoginRequest();
        req.setLogin(TEST_LOGIN);
        req.setPassword("wrong");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad creds"));

        assertThrows(BadCredentialsException.class, () -> userService.login(req));
    }

    @Test
    void getPublicProfile_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getPublicProfile("unknown"));
    }

    @Test
    void getPublicProfile_shouldThrowWhenUserBlocked() {
        testUser.setBlocked(true);
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        assertThrows(UserBlockedException.class, () -> userService.getPublicProfile(TEST_LOGIN));
    }

    @Test
    void getMyProfile_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getMyProfile(TEST_LOGIN));
    }

    @Test
    void getMyProfile_shouldThrowWhenUserBlocked() {
        testUser.setBlocked(true);
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        assertThrows(UserBlockedException.class, () -> userService.getMyProfile(TEST_LOGIN));
    }

    @Test
    void updateMyProfile_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        UserUpdateRequest req = new UserUpdateRequest();
        assertThrows(UserNotFoundException.class, () -> userService.updateMyProfile(TEST_LOGIN, req));
    }

    @Test
    void updateMyProfile_shouldThrowWhenUserBlocked() {
        testUser.setBlocked(true);
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        UserUpdateRequest req = new UserUpdateRequest();
        assertThrows(UserBlockedException.class, () -> userService.updateMyProfile(TEST_LOGIN, req));
    }

    @Test
    void deleteMyProfile_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteMyProfile(TEST_LOGIN));
    }

    @Test
    void deleteMyProfile_shouldThrowWhenUserAlreadyBlocked() {
        testUser.setBlocked(true);
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        assertThrows(UserBlockedException.class, () -> userService.deleteMyProfile(TEST_LOGIN));
        verify(userRepository, never()).save(any());
    }

    @Test
    void balanceUp_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        BalanceUpRequest req = new BalanceUpRequest();
        assertThrows(UserNotFoundException.class, () -> userService.balanceUp(TEST_LOGIN, req));
    }

    @Test
    void balanceUp_shouldThrowWhenUserBlocked() {
        testUser.setBlocked(true);
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        BalanceUpRequest req = new BalanceUpRequest();
        assertThrows(UserBlockedException.class, () -> userService.balanceUp(TEST_LOGIN, req));
    }

    @Test
    void getFullProfile_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getFullProfile(TEST_LOGIN));
    }

    @Test
    void changeUserRole_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.changeUserRole(TEST_LOGIN, "MANAGER"));
    }

    @Test
    void changeUserRole_shouldThrowWhenInvalidRole() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(testUser));
        assertThrows(IllegalArgumentException.class, () -> userService.changeUserRole(TEST_LOGIN, "INVALID_ROLE"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void blockUser_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.blockUser(TEST_LOGIN));
    }

    @Test
    void unblockUser_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.unblockUser(TEST_LOGIN));
    }

    @Test
    void getInternalUserByLogin_shouldThrowWhenUserNotFound() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getInternalUserByLogin(TEST_LOGIN));
    }

    @Test
    void getInternalUserById_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(testId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getInternalUserById(testId));
    }
}