package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.user.*;
import com.likelion.mutsasns.exception.AbstractBaseException;
import com.likelion.mutsasns.exception.badrequest.UpdateUserRoleException;
import com.likelion.mutsasns.exception.conflict.DuplicateUsernameException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPasswordException;
import com.likelion.mutsasns.repository.UserRepository;
import com.likelion.mutsasns.security.provider.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.likelion.mutsasns.exception.ErrorCode.*;
import static com.likelion.mutsasns.support.TestConstant.MOCK_TOKEN;
import static com.likelion.mutsasns.support.fixture.UserFixture.ADMIN;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    @DisplayName("로그인 : 정상")
    void login() {
        final LoginRequest loginRequest = USER.loginRequest();

        given(jwtProvider.generateToken(any(User.class))).willReturn(MOCK_TOKEN);
        given(userRepository.findByUsername(loginRequest.getUserName())).willReturn(Optional.of(USER.init()));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        LoginResponse loginResponse = userService.login(loginRequest);

        assertEquals(MOCK_TOKEN, loginResponse.getJwt());
        verify(jwtProvider).generateToken(any(User.class));
        verify(userRepository).findByUsername(loginRequest.getUserName());
    }

    @Test
    @DisplayName("로그인 : 실패 - 해당 유저 없음")
    void login_user_not_found() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        AbstractBaseException e = assertThrows(UserNotFoundException.class, () -> userService.login(USER.loginRequest()));
        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("로그인 : 실패 - 비밀번호 불일치")
    void login_invalid_password() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(USER.init()));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        AbstractBaseException e = assertThrows(InvalidPasswordException.class, () -> userService.login(USER.loginRequest()));
        assertEquals(INVALID_PASSWORD, e.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 : 정상")
    void join() {
        final User user = USER.init();
        final JoinRequest joinRequest = USER.joinRequest();

        given(userRepository.existsByUsername(user.getUsername())).willReturn(false);
        given(passwordEncoder.encode(joinRequest.getPassword())).willReturn(joinRequest.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);

        JoinResponse result = userService.join(joinRequest);

        assertEquals(user.getId(), result.getUserId());
        assertEquals(user.getUsername(), result.getUserName());

        verify(userRepository).existsByUsername(joinRequest.getUserName());
        verify(passwordEncoder).encode(joinRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 : 실패 - 아이디 중복")
    void join_duplicate_username() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        AbstractBaseException e = assertThrows(DuplicateUsernameException.class, () -> userService.join(USER.joinRequest()));
        assertEquals(DUPLICATED_USERNAME, e.getErrorCode());
    }

    @Test
    @DisplayName("권한변경 : 정상")
    void updateRole() {
        final User admin = ADMIN.init();
        final User user = USER.init();
        final UpdateUserRoleRequest updateUserRoleRequest = USER.updateRoleRequest(Role.ROLE_ADMIN);

        given(userRepository.findByUsername(admin.getUsername())).willReturn(Optional.of(admin));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UserDetailResponse result = userService.updateRole(admin.getUsername(), user.getId(), updateUserRoleRequest);

        assertEquals(user.getId(), result.getUserId());
        assertEquals(Role.ROLE_ADMIN, result.getRole());
    }

    @Test
    @DisplayName("권한변경 : 실패 - 본인 권한 변경 시도")
    void updateRole_update_user_role_myself() {
        final User admin = ADMIN.init();
        final UpdateUserRoleRequest updateUserRoleRequest = ADMIN.updateRoleRequest(Role.ROLE_USER);

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(ADMIN.init()));

        AbstractBaseException e = assertThrows(UpdateUserRoleException.class, () -> userService.updateRole(admin.getUsername(), admin.getId(), updateUserRoleRequest));
        assertEquals(INVALID_UPDATE_USER_ROLE, e.getErrorCode());
    }
}