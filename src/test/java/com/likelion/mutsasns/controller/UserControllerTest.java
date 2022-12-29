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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.likelion.mutsasns.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestWithSecurity(controllers = UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String SUCCESS = "SUCCESS";
    private final String ERROR = "ERROR";
    private final String BEARER = "Bearer ";

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
            .role(Role.ROLE_ADMIN)
            .build();
    private final Authentication AUTHENTICATION = new UsernamePasswordAuthenticationToken(USER, MOCK_TOKEN, USER.getAuthorities());
    private final Authentication ADMIN_AUTHENTICATION = new UsernamePasswordAuthenticationToken(ADMIN, MOCK_TOKEN, ADMIN.getAuthorities());
    private final LoginRequest LOGIN_REQUEST = new LoginRequest(USERNAME, PASSWORD);
    private final LoginResponse LOGIN_RESPONSE = new LoginResponse(MOCK_TOKEN);
    private final JoinRequest JOIN_REQUEST = new JoinRequest(USERNAME, PASSWORD);
    private final JoinResponse JOIN_RESPONSE = new JoinResponse(USERNAME, USER_ID);
    private final UpdateUserRoleRequest UPDATE_USER_ROLE_REQUEST = new UpdateUserRoleRequest("admin");
    private final UserDetailResponse USER_DETAIL_RESPONSE = new UserDetailResponse(USER_ID, USERNAME, Role.ROLE_ADMIN);
    @Test
    @DisplayName("로그인 : 정상")
    void login() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenReturn(LOGIN_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(LOGIN_REQUEST)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value(SUCCESS))
            .andExpect(jsonPath("$.result.jwt").value(MOCK_TOKEN));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("로그인 : 실패 - 해당 아이디 없음")
    void login_user_not_found() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LOGIN_REQUEST)))
                .andExpect(status().is(USER_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(USER_NOT_FOUND.getMessage()));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("로그인 : 실패 - 비밀번호 불일치")
    void login_invalid_password() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenThrow(new InvalidPasswordException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LOGIN_REQUEST)))
                .andExpect(status().is(INVALID_PASSWORD.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_PASSWORD.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_PASSWORD.getMessage()));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("회원가입 : 정상")
    void join() throws Exception {
        when(userService.join(any(JoinRequest.class))).thenReturn(JOIN_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(JOIN_REQUEST)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value(SUCCESS))
            .andExpect(jsonPath("$.result.userId").value(USER_ID))
            .andExpect(jsonPath("$.result.userName").value(USERNAME));

        verify(userService).join(any(JoinRequest.class));
    }

    @Test
    @DisplayName("회원가입 : 실패 - 아이디 중복")
    void join_duplicate_username() throws Exception {
        when(userService.join(any(JoinRequest.class))).thenThrow(new DuplicateUsernameException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(JOIN_REQUEST)))
                .andExpect(status().is(DUPLICATED_USERNAME.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(DUPLICATED_USERNAME.name()))
                .andExpect(jsonPath("$.result.message").value(DUPLICATED_USERNAME.getMessage()));

        verify(userService).join(any(JoinRequest.class));
    }

    @Test
    @DisplayName("권한 변경 : 정상")
    void updateUserRole() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(ADMIN_AUTHENTICATION);
        when(userService.updateRole(eq(ADMIN_USERNAME), eq(USER_ID), any(UpdateUserRoleRequest.class))).thenReturn(USER_DETAIL_RESPONSE);

        System.out.println(ADMIN_AUTHENTICATION.getAuthorities());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/" + USER_ID + "/role/change")
                .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UPDATE_USER_ROLE_REQUEST)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value(SUCCESS))
            .andExpect(jsonPath("$.result.userId").value(USER_ID))
            .andExpect(jsonPath("$.result.role").value(Role.ROLE_ADMIN.name()));

        verify(userService).updateRole(eq(ADMIN_USERNAME), eq(USER_ID), any(UpdateUserRoleRequest.class));
    }

    @Test
    @DisplayName("권한 변경 : 실패 - 권한 없음")
    void updateUserRole_no_admin() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/" + USER_ID + "/role/change")
                .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UPDATE_USER_ROLE_REQUEST)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.resultCode").value(ERROR))
            .andExpect(jsonPath("$.result.errorCode").value(INVALID_PERMISSION.name()))
            .andExpect(jsonPath("$.result.message").value(INVALID_PERMISSION.getMessage()));

        verify(userService, never()).updateRole(anyString(), eq(USER_ID), any(UpdateUserRoleRequest.class));
    }
}