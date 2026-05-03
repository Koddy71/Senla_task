package com.sen.dto.request;

import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(max = 100)
    private String fullname;
    
    @Size(max = 20)
    private String phone;

    public UserUpdateRequest(){}

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
