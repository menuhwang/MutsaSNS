package com.likelion.mutsasns.support.fixture;

import com.likelion.mutsasns.domain.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static com.likelion.mutsasns.support.fixture.UserFixture.ADMIN;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;

public enum AuthenticationFixture {
    AUTHENTICATION(USER.init()),
    ADMIN_AUTHENTICATION(ADMIN.init());

    private final User user;

    AuthenticationFixture(User user) {
        this.user = user;
    }

    public Authentication init() {
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }
}
