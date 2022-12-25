package com.likelion.mutsasns.dto.user;

import com.likelion.mutsasns.domain.user.Role;
import lombok.Getter;

@Getter
public class UpdateUserRoleRequest {
    private Role role;

    private UpdateUserRoleRequest() {
    }

    public UpdateUserRoleRequest(Role role) {
        this.role = role;
    }
}
