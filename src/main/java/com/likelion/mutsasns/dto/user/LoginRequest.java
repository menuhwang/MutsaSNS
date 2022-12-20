package com.likelion.mutsasns.dto.user;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String userName;
    private String password;

    private LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.userName = username;
        this.password = password;
    }
}
