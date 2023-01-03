package com.likelion.mutsasns.support.fixture;

import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.user.*;

public enum UserFixture {
    ADMIN(0L, "admin", "password", Role.ROLE_ADMIN),
    USER(1L, "user1", "password", Role.ROLE_USER),
    OTHER_USER(2L, "user2", "password", Role.ROLE_USER);

    private final Long id;
    private final String username;
    private final String password;
    private final Role role;

    UserFixture(Long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User init() {
        return User.builder()
                .id(id)
                .username(username)
                .password(password)
                .role(role)
                .build();
    }

    public LoginRequest loginRequest() {
        return new LoginRequest(username, password);
    }

    public LoginResponse loginResponse(String token) {
        return new LoginResponse(token);
    }

    public JoinRequest joinRequest() {
        return new JoinRequest(username, password);
    }

    public JoinResponse joinResponse() {
        return new JoinResponse(username, id);
    }

    public UpdateUserRoleRequest updateRoleRequest(Role role) {
        return new UpdateUserRoleRequest(role.name());
    }

    public UserDetailResponse userDetailResponse(Role role) {
        return UserDetailResponse.builder()
                .id(id)
                .userName(username)
                .role(role)
                .build();
    }
}
