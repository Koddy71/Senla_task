package com.sen.dto.request;

import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(max = 100)
    private String fullName;
    
    @Size(max = 20)
    private String phone;

    public UserUpdateRequest(){}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
