package com.likelion.mutsasns.dto.user;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String username;
    private String password;

    private LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
