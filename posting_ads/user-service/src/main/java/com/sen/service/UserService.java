package com.sen.service;

import java.util.List;

import com.sen.dto.internal.UserInternal;
import com.sen.dto.request.BalanceUpRequest;
import com.sen.dto.request.LoginRequest;
import com.sen.dto.request.RegistrationRequest;
import com.sen.dto.request.UserFilterRequest;
import com.sen.dto.request.UserUpdateRequest;
import com.sen.dto.response.TokenResponse;
import com.sen.dto.response.PrivateUserResponse;
import com.sen.dto.response.PublicUserResponse;

public interface UserService {
    PrivateUserResponse register(RegistrationRequest request);

    TokenResponse login(LoginRequest request);

    //USER
    PublicUserResponse getPublicProfile(String login);

    PrivateUserResponse getMyProfile(String myLogin);

    PrivateUserResponse balanceUp(String myLogin, BalanceUpRequest request);

    PrivateUserResponse updateMyProfile(String myLogin, UserUpdateRequest request);

    void deleteMyProfile(String myLogin);

    //ADMIN
    PrivateUserResponse getFullProfile(String login);

    List<PrivateUserResponse> getAllUsers(UserFilterRequest filter);

    void changeUserRole(String login, String newRole);

    void blockUser(String login);

    void unblockUser(String login);

    //Internal
    UserInternal getInternalUser(String login);
}
