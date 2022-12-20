package com.likelion.mutsasns.dto.user;

import com.likelion.mutsasns.domain.user.User;
import lombok.Getter;

@Getter
public class JoinResponse {
    private String userName;
    private Long userId;

    private JoinResponse() {
    }

    public JoinResponse(String userName, Long userId) {
        this.userName = userName;
        this.userId = userId;
    }

    public static JoinResponse of(User user) {
        return new JoinResponse(user.getUsername(), user.getId());
    }
}
