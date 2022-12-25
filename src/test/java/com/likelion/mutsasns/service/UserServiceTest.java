package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.user.*;
import com.likelion.mutsasns.exception.badrequest.UpdateUserRoleException;
import com.likelion.mutsasns.exception.conflict.DuplicateUsernameException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPasswordException;
import com.likelion.mutsasns.repository.UserRepository;
import com.likelion.mutsasns.security.provider.JwtProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private final Long ADMIN_ID = 2L;
    private final String USERNAME = "tester";
    private final String ADMIN_USERNAME = "admin";
    private final String PASSWORD = "password";
    private final User USER = User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .password(PASSWORD)
            .build();
    private final User ADMIN = User.builder()
            .id(ADMIN_ID)
            .username(ADMIN_USERNAME)
            .password(PASSWORD)
            .build();
    private final LoginRequest LOGIN_REQUEST = new LoginRequest(USERNAME, PASSWORD);
    private final JoinRequest JOIN_REQUEST = new JoinRequest(USERNAME, PASSWORD);
    private final UpdateUserRoleRequest UPDATE_USER_ROLE_REQUEST = new UpdateUserRoleRequest(Role.ROLE_ADMIN);

    @Test
    void login() {
        given(jwtProvider.generateToken(any(User.class))).willReturn(MOCK_TOKEN);
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        LoginResponse loginResponse = userService.login(LOGIN_REQUEST);

        assertEquals(MOCK_TOKEN, loginResponse.getJwt());
        verify(jwtProvider).generateToken(any(User.class));
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void login_user_not_found() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.login(LOGIN_REQUEST));
    }

    @Test
    void login_invalid_password() {
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(InvalidPasswordException.class, () -> userService.login(LOGIN_REQUEST));
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

    @Test
    void join_duplicate_username() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> userService.join(JOIN_REQUEST));
    }

    @Test
    void updateRole() {
        given(userRepository.findByUsername(ADMIN_USERNAME)).willReturn(Optional.of(ADMIN));
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(USER));

        UserDetailResponse result = userService.updateRole(ADMIN_USERNAME, USER_ID, UPDATE_USER_ROLE_REQUEST);

        assertEquals(USER_ID, result.getUserId());
        assertEquals(UPDATE_USER_ROLE_REQUEST.getRole(), result.getRole());
    }

    @Test
    void updateRole_update_user_role_myself() {
        given(userRepository.findByUsername(ADMIN_USERNAME)).willReturn(Optional.of(ADMIN));

        assertThrows(UpdateUserRoleException.class, () -> userService.updateRole(ADMIN_USERNAME, ADMIN_ID, UPDATE_USER_ROLE_REQUEST));
    }
}