package com.sen.controller;

import com.sen.dto.request.BalanceUpRequest;
import com.sen.dto.request.LoginRequest;
import com.sen.dto.request.RegistrationRequest;
import com.sen.dto.request.UserFilterRequest;
import com.sen.dto.request.UserUpdateRequest;
import com.sen.dto.response.PrivateUserResponse;
import com.sen.dto.response.PublicUserResponse;
import com.sen.dto.response.TokenResponse;
import com.sen.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void register_shouldReturn201() {
        PrivateUserResponse resp = new PrivateUserResponse();
        resp.setLogin("newuser");
        when(userService.register(any(RegistrationRequest.class))).thenReturn(resp);

        RegistrationRequest req = new RegistrationRequest();
        req.setLogin("newuser");
        req.setPassword("pass");
        req.setFullName("New");

        ResponseEntity<PrivateUserResponse> result = userController.register(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("newuser", result.getBody().getLogin());
    }

    @Test
    void login_shouldReturnToken() {
        when(userService.login(any(LoginRequest.class))).thenReturn(new TokenResponse("token"));

        LoginRequest req = new LoginRequest();
        req.setLogin("user");
        req.setPassword("pass");

        ResponseEntity<TokenResponse> result = userController.login(req);

        assertEquals("token", result.getBody().getToken());
    }

    @Test
    void getPublicProfile_shouldReturnPublicResponse() {
        PublicUserResponse resp = new PublicUserResponse();
        resp.setLogin("ivan");
        when(userService.getPublicProfile("ivan")).thenReturn(resp);

        ResponseEntity<PublicUserResponse> result = userController.getPublicProfile("ivan");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("ivan", result.getBody().getLogin());
    }

    @Test
    void getMyProfile_shouldReturnPrivateResponse() {
        PrivateUserResponse resp = new PrivateUserResponse();
        resp.setLogin("me");
        when(userService.getMyProfile()).thenReturn(resp);

        ResponseEntity<PrivateUserResponse> result = userController.getMyProfile();

        assertEquals("me", result.getBody().getLogin());
    }

    @Test
    void updateMyProfile_shouldCallService() {
        PrivateUserResponse resp = new PrivateUserResponse();
        when(userService.updateMyProfile(any(UserUpdateRequest.class))).thenReturn(resp);

        UserUpdateRequest req = new UserUpdateRequest();
        req.setFullName("Updated");

        ResponseEntity<PrivateUserResponse> result = userController.updateMyProfile(req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService).updateMyProfile(req);
    }

    @Test
    void upBalance_shouldCallService() {
        PrivateUserResponse resp = new PrivateUserResponse();
        resp.setBalance(new BigDecimal("150.00"));
        when(userService.balanceUp(any(BalanceUpRequest.class))).thenReturn(resp);

        BalanceUpRequest req = new BalanceUpRequest();
        req.setAmount(new BigDecimal("50.00"));

        ResponseEntity<PrivateUserResponse> result = userController.upBalance(req);

        assertEquals(new BigDecimal("150.00"), result.getBody().getBalance());
    }

    @Test
    void getFullProfile_shouldReturnPrivateResponse() {
        PrivateUserResponse resp = new PrivateUserResponse();
        resp.setLogin("admin");
        when(userService.getFullProfile("admin")).thenReturn(resp);

        ResponseEntity<PrivateUserResponse> result = userController.getFullProfile("admin");

        assertEquals("admin", result.getBody().getLogin());
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userService.getAllUsers(any(UserFilterRequest.class))).thenReturn(List.of(new PrivateUserResponse()));

        ResponseEntity<List<PrivateUserResponse>> result = userController.getAllUsers(new UserFilterRequest());

        assertEquals(1, result.getBody().size());
    }

    @Test
    void changeRole_shouldReturn204() {
        doNothing().when(userService).changeUserRole(anyString(), anyString());

        ResponseEntity<Void> result = userController.changeRole("user", "MANAGER");

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).changeUserRole("user", "MANAGER");
    }

    @Test
    void blockUser_shouldReturn204() {
        doNothing().when(userService).blockUser("baduser");

        ResponseEntity<Void> result = userController.blockUser("baduser");

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void unblockUser_shouldReturn204() {
        doNothing().when(userService).unblockUser("gooduser");

        ResponseEntity<Void> result = userController.unblockUser("gooduser");

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
