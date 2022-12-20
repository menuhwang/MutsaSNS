package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.user.JoinRequest;
import com.likelion.mutsasns.dto.user.JoinResponse;
import com.likelion.mutsasns.dto.user.LoginRequest;
import com.likelion.mutsasns.dto.user.LoginResponse;
import com.likelion.mutsasns.repository.UserRepository;
import com.likelion.mutsasns.security.provider.JwtProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final JwtProvider jwtProvider = Mockito.mock(JwtProvider.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService userService = new UserService(passwordEncoder, jwtProvider, userRepository);

    private final String MOCK_TOKEN = "mockJwtToken";
    private final Long USER_ID = 1L;
    private final String USERNAME = "tester";
    private final String PASSWORD = "password";
    private final User USER = User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .password(PASSWORD)
            .build();
    private final LoginRequest LOGIN_REQUEST = new LoginRequest(USERNAME, PASSWORD);
    private final JoinRequest JOIN_REQUEST = new JoinRequest(USERNAME, PASSWORD);

    @Test
    void login() {
        given(jwtProvider.generateToken(any(User.class))).willReturn(MOCK_TOKEN);
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));
        LoginResponse loginResponse = userService.login(LOGIN_REQUEST);

        assertEquals(MOCK_TOKEN, loginResponse.getJwt());
        verify(jwtProvider).generateToken(any(User.class));
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void join() {
        given(userRepository.existsByUsername(USERNAME)).willReturn(false);
        given(passwordEncoder.encode(PASSWORD)).willReturn(PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(USER);

        JoinResponse result = userService.join(JOIN_REQUEST);

        assertEquals(USER_ID, result.getUserId());
        assertEquals(USERNAME, result.getUserName());

        verify(userRepository).existsByUsername(USERNAME);
        verify(passwordEncoder).encode(PASSWORD);
        verify(userRepository).save(any(User.class));
    }
}