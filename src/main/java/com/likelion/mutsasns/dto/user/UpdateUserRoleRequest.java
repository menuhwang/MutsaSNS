package com.likelion.mutsasns.dto.user;

import lombok.Getter;

@Getter
public class UpdateUserRoleRequest {
    private String role;

    private UpdateUserRoleRequest() {
    }

    public UpdateUserRoleRequest(String role) {
        this.role = role;
    }
}
