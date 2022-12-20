package com.likelion.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.dto.user.JoinRequest;
import com.likelion.mutsasns.dto.user.JoinResponse;
import com.likelion.mutsasns.dto.user.LoginRequest;
import com.likelion.mutsasns.dto.user.LoginResponse;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPasswordException;
import com.likelion.mutsasns.security.config.WebSecurityConfig;
import com.likelion.mutsasns.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)})
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String MOCK_TOKEN = "mockJwtToken";
    private final Long USER_ID = 1L;
    private final String USERNAME = "tester";
    private final String PASSWORD = "password";
    private final LoginRequest LOGIN_REQUEST = new LoginRequest(USERNAME, PASSWORD);
    private final LoginResponse LOGIN_RESPONSE = new LoginResponse(MOCK_TOKEN);
    private final JoinRequest JOIN_REQUEST = new JoinRequest(USERNAME, PASSWORD);
    private final JoinResponse JOIN_RESPONSE = new JoinResponse(USERNAME, USER_ID);
    @Test
    void login() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenReturn(LOGIN_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(LOGIN_REQUEST)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jwt").value(MOCK_TOKEN));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    void login_user_not_found() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LOGIN_REQUEST)))
                .andExpect(status().isNotFound());

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    void login_invalid_password() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenThrow(new InvalidPasswordException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LOGIN_REQUEST)))
                .andExpect(status().isUnauthorized());

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    void join() throws Exception {
        when(userService.join(any(JoinRequest.class))).thenReturn(JOIN_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(JOIN_REQUEST)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result.userId").value(USER_ID))
            .andExpect(jsonPath("$.result.userName").value(USERNAME));

        verify(userService).join(any(JoinRequest.class));
    }
}