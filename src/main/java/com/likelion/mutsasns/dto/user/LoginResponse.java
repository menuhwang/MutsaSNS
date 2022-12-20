package com.likelion.mutsasns.dto.user;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String jwt;

    private LoginResponse() {
    }

    public LoginResponse(String jwt) {
        this.jwt = jwt;
    }
}
