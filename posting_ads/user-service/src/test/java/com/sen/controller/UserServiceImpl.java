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
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private UserDetails userDetails;
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
        String login = "me";
        when(userDetails.getUsername()).thenReturn(login);
        PrivateUserResponse resp = new PrivateUserResponse();
        resp.setLogin(login);
        when(userService.getMyProfile(login)).thenReturn(resp);

        ResponseEntity<PrivateUserResponse> result = userController.getMyProfile(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(login, result.getBody().getLogin());
        verify(userService).getMyProfile(login);
    }

    @Test
    void updateMyProfile_shouldCallService() {
        String login = "me";
        when(userDetails.getUsername()).thenReturn(login);
        PrivateUserResponse resp = new PrivateUserResponse();
        resp.setLogin(login);
        when(userService.updateMyProfile(eq(login), any(UserUpdateRequest.class))).thenReturn(resp);

        UserUpdateRequest req = new UserUpdateRequest();
        req.setFullname("Updated");

        ResponseEntity<PrivateUserResponse> result = userController.updateMyProfile(userDetails, req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService).updateMyProfile(eq(login), any(UserUpdateRequest.class));
    }

    @Test
    void upBalance_shouldCallService() {
        String login = "me";
        when(userDetails.getUsername()).thenReturn(login);
        PrivateUserResponse resp = new PrivateUserResponse();
        resp.setBalance(new BigDecimal("150.00"));
        when(userService.balanceUp(eq(login), any(BalanceUpRequest.class))).thenReturn(resp);

        BalanceUpRequest req = new BalanceUpRequest();
        req.setAmount(new BigDecimal("50.00"));

        ResponseEntity<PrivateUserResponse> result = userController.upBalance(userDetails, req);

        assertEquals(new BigDecimal("150.00"), result.getBody().getBalance());
        verify(userService).balanceUp(eq(login), any(BalanceUpRequest.class));
    }

    @Test
    void deleteMyProfile_shouldReturn204() {
        String login = "me";
        when(userDetails.getUsername()).thenReturn(login);
        doNothing().when(userService).deleteMyProfile(login);

        ResponseEntity<Void> result = userController.deleteMyProfile(userDetails);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService).deleteMyProfile(login);
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