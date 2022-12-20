package com.likelion.mutsasns.dto.user;

import com.likelion.mutsasns.domain.user.User;
import lombok.Getter;

@Getter
public class JoinRequest {
    private String userName;
    private String password;

    private JoinRequest() {
    }

    public JoinRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User toEntity(String encoded) {
        return User.builder()
                .username(userName)
                .password(encoded)
                .build();
    }
}
