package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.repository.UserRepository;
import com.likelion.mutsasns.security.provider.JwtProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class UserServiceTest {
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final JwtProvider jwtProvider = Mockito.mock(JwtProvider.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService userService = new UserService(passwordEncoder, jwtProvider, userRepository);

    private final String MOCK_TOKEN = "mockJwtToken";
    private final String USERNAME = "tester";
    private final String PASSWORD = "password";
    private final LoginRequest loginRequest = new LoginRequest(USERNAME, PASSWORD);

    @Test
    void login() {
        given(passwordEncoder.encode(PASSWORD)).willReturn(PASSWORD);
        given(jwtProvider.generateToken(any(User.class))).willReturn(MOCK_TOKEN);
        LoginResponse loginResponse = userService.login(loginRequest);

        assertEquals(MOCK_TOKEN, loginResponse.getJwt());
        verify(passwordEncoder).encode(PASSWORD);
        verify(jwtProvider).generateToken(any(User.class));
    }
}