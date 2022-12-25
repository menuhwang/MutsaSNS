package com.likelion.mutsasns.domain.user;

import com.likelion.mutsasns.exception.badrequest.BadConstantException;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    public static Role of(String name) {
        for (Role role : values()) {
            if (role.name().contains(name)) return role;
        }
        throw new BadConstantException(String.format("잘못된 인자입니다. [%s]", name));
    }
}
