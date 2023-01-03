package com.likelion.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
import com.likelion.mutsasns.dto.comment.CommentRequest;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.security.provider.JwtProvider;
import com.likelion.mutsasns.service.CommentService;
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

import static com.likelion.mutsasns.exception.ErrorCode.INVALID_TOKEN;
import static com.likelion.mutsasns.exception.ErrorCode.POST_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestWithSecurity(controllers = CommentController.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @MockBean
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String SUCCESS = "SUCCESS";
    private final String ERROR = "ERROR";
    private final String BEARER = "Bearer ";
    private final String MOCK_TOKEN = "mockJwtToken";
    private final Long USER_ID = 1L;
    private final String USERNAME = "tester";
    private final String PASSWORD = "password";
    private final User USER = User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .password(PASSWORD)
            .build();
    private final Authentication AUTHENTICATION = new UsernamePasswordAuthenticationToken(USER, MOCK_TOKEN, USER.getAuthorities());
    private final Long POST_ID = 1L;
    private final Long COMMENT_ID = 1L;
    private final String COMMENT_CONTENT = "content";
    private final CommentRequest COMMENT_REQUEST = new CommentRequest(COMMENT_CONTENT);
    private final CommentDetailResponse COMMENT_DETAIL_RESPONSE = CommentDetailResponse.builder()
            .id(COMMENT_ID)
            .comment(COMMENT_CONTENT)
            .userName(USERNAME)
            .postId(POST_ID)
            .build();

    @Test
    @DisplayName("작성 : 정상")
    void create() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION);
        given(commentService.create(eq(POST_ID), eq(USERNAME), any(CommentRequest.class))).willReturn(COMMENT_DETAIL_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + POST_ID + "/comments")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(COMMENT_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.result.comment").value(COMMENT_CONTENT))
                .andExpect(jsonPath("$.result.userName").value(USERNAME))
                .andExpect(jsonPath("$.result.postId").value(POST_ID));

        verify(commentService).create(eq(POST_ID), eq(USERNAME), any(CommentRequest.class));
    }

    @Test
    @DisplayName("작성 : 실패 - 해당 게시글 없음")
    void create_post_not_found() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION);
        given(commentService.create(eq(POST_ID), eq(USERNAME), any(CommentRequest.class))).willThrow(new PostNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + POST_ID + "/comments")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(COMMENT_REQUEST)))
                .andExpect(status().is(POST_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("작성 : 실패 - 로그인하지 않은 경우, 잘못된 토큰")
    void create_invalid_token() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + POST_ID + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(COMMENT_REQUEST)))
                .andExpect(status().is(INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()));
    }
}