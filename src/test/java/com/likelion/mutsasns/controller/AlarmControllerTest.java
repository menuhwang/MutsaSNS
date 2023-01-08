package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.alarm.AlarmResponse;
import com.likelion.mutsasns.security.provider.JwtProvider;
import com.likelion.mutsasns.service.AlarmService;
import com.likelion.mutsasns.support.annotation.WebMvcTestWithSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.likelion.mutsasns.domain.alarm.AlarmType.NEW_COMMENT_ON_POST;
import static com.likelion.mutsasns.domain.alarm.AlarmType.NEW_LIKE_ON_POST;
import static com.likelion.mutsasns.exception.ErrorCode.INVALID_TOKEN;
import static com.likelion.mutsasns.support.TestConstant.*;
import static com.likelion.mutsasns.support.fixture.AlarmFixture.ALARM;
import static com.likelion.mutsasns.support.fixture.AuthenticationFixture.AUTHENTICATION;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestWithSecurity(controllers = AlarmController.class)
class AlarmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AlarmService alarmService;
    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("알람 조회 : 정상")
    void findMyAlarms() throws Exception {
        final User user = USER.init();
        final Page<AlarmResponse> page = new PageImpl<>(List.of(
                ALARM.init(1L, NEW_COMMENT_ON_POST),
                ALARM.init(2L, NEW_LIKE_ON_POST),
                ALARM.init(3L, NEW_COMMENT_ON_POST)
        )).map(AlarmResponse::of);

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(alarmService.findMyAlarms(eq(user.getUsername()), any(Pageable.class))).willReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/alarms")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.pageable").exists())
                .andExpect(jsonPath("$.result.size").exists());

        verify(alarmService).findMyAlarms(eq(user.getUsername()), any(Pageable.class));
    }

    @Test
    @DisplayName("알람 조회 : 실패 - 인증 실패")
    void findMyAlarms_invalid_token() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/alarms")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()));

        verify(alarmService, never()).findMyAlarms(anyString(), any(Pageable.class));
    }
}