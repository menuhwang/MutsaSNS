package com.likelion.mutsasns.dto.user;

import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDetailResponse {
    private Long userId;
    private String userName;
    private Role role;

    private UserDetailResponse() {
    }

    @Builder
    public UserDetailResponse(Long id, String userName, Role role) {
        this.userId = id;
        this.userName = userName;
        this.role = role;
    }

    public static UserDetailResponse of(User user) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
