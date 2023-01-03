package com.likelion.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.user.*;
import com.likelion.mutsasns.exception.conflict.DuplicateUsernameException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPasswordException;
import com.likelion.mutsasns.security.provider.JwtProvider;
import com.likelion.mutsasns.service.UserService;
import com.likelion.mutsasns.support.annotation.WebMvcTestWithSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.likelion.mutsasns.exception.ErrorCode.*;
import static com.likelion.mutsasns.support.TestConstant.*;
import static com.likelion.mutsasns.support.fixture.UserFixture.ADMIN;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestWithSecurity(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("로그인 : 정상")
    void login() throws Exception {
        final LoginRequest loginRequest = USER.loginRequest();
        final LoginResponse loginResponse = USER.loginResponse(MOCK_TOKEN);

        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value(SUCCESS))
            .andExpect(jsonPath("$.result.jwt").value(MOCK_TOKEN));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("로그인 : 실패 - 해당 아이디 없음")
    void login_user_not_found() throws Exception {
        final LoginRequest loginRequest = USER.loginRequest();

        when(userService.login(any(LoginRequest.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(USER_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(USER_NOT_FOUND.getMessage()));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("로그인 : 실패 - 비밀번호 불일치")
    void login_invalid_password() throws Exception {
        final LoginRequest loginRequest = USER.loginRequest();

        when(userService.login(any(LoginRequest.class))).thenThrow(new InvalidPasswordException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(INVALID_PASSWORD.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_PASSWORD.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_PASSWORD.getMessage()));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("회원가입 : 정상")
    void join() throws Exception {
        final User user = USER.init();
        final JoinRequest joinRequest = USER.joinRequest();
        final JoinResponse joinResponse = USER.joinResponse();

        when(userService.join(any(JoinRequest.class))).thenReturn(joinResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value(SUCCESS))
            .andExpect(jsonPath("$.result.userId").value(user.getId()))
            .andExpect(jsonPath("$.result.userName").value(user.getUsername()));

        verify(userService).join(any(JoinRequest.class));
    }

    @Test
    @DisplayName("회원가입 : 실패 - 아이디 중복")
    void join_duplicate_username() throws Exception {
        final JoinRequest joinRequest = USER.joinRequest();

        when(userService.join(any(JoinRequest.class))).thenThrow(new DuplicateUsernameException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().is(DUPLICATED_USERNAME.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(DUPLICATED_USERNAME.name()))
                .andExpect(jsonPath("$.result.message").value(DUPLICATED_USERNAME.getMessage()));

        verify(userService).join(any(JoinRequest.class));
    }

    @DisplayName("권한 변경 : 정상")
    @Test
    void updateUserRole() throws Exception {
        final User admin = ADMIN.init();
        final User user = USER.init();
        final UpdateUserRoleRequest updateUserRoleRequest = USER.updateRoleRequest(Role.ROLE_ADMIN);
        final UserDetailResponse updateRoleResponse = USER.userDetailResponse(Role.ROLE_ADMIN);
        final Authentication adminAuthentication = new UsernamePasswordAuthenticationToken(admin, admin.getPassword(), admin.getAuthorities());

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(adminAuthentication);
        when(userService.updateRole(eq(admin.getUsername()), eq(user.getId()), any(UpdateUserRoleRequest.class))).thenReturn(updateRoleResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/" + user.getId() + "/role/change")
                .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRoleRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value(SUCCESS))
            .andExpect(jsonPath("$.result.userId").value(user.getId()))
            .andExpect(jsonPath("$.result.role").value(Role.ROLE_ADMIN.name()));

        verify(userService).updateRole(eq(admin.getUsername()), eq(user.getId()), any(UpdateUserRoleRequest.class));
    }

    @Test
    @DisplayName("권한 변경 : 실패 - 권한 없음")
    void updateUserRole_no_admin() throws Exception {
        final User user = USER.init();
        final Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        final UpdateUserRoleRequest updateUserRoleRequest = USER.updateRoleRequest(Role.ROLE_ADMIN);

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(authentication);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/" + user.getId() + "/role/change")
                .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRoleRequest)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.resultCode").value(ERROR))
            .andExpect(jsonPath("$.result.errorCode").value(INVALID_PERMISSION.name()))
            .andExpect(jsonPath("$.result.message").value(INVALID_PERMISSION.getMessage()));

        verify(userService, never()).updateRole(anyString(), eq(user.getId()), any(UpdateUserRoleRequest.class));
    }
}