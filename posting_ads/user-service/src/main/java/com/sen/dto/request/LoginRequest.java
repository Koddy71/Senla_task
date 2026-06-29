package com.sen.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    public LoginRequest(){}

    public void setLogin(String login){
        this.login=login;
    }

    public String getLogin(){
        return login;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public String getPassword(){
        return password;
    }
}
