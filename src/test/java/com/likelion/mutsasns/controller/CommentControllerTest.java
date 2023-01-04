package com.likelion.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.domain.post.Post;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.likelion.mutsasns.exception.ErrorCode.INVALID_TOKEN;
import static com.likelion.mutsasns.exception.ErrorCode.POST_NOT_FOUND;
import static com.likelion.mutsasns.support.TestConstant.*;
import static com.likelion.mutsasns.support.fixture.AuthenticationFixture.AUTHENTICATION;
import static com.likelion.mutsasns.support.fixture.CommentFixture.COMMENT;
import static com.likelion.mutsasns.support.fixture.PostFixture.POST;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;
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

    @Test
    @DisplayName("작성 : 정상")
    void create() throws Exception {
        final User user = USER.init();
        final Post post = POST.init(user);
        final CommentRequest commentRequest = COMMENT.createRequest();
        final CommentDetailResponse commentDetailResponse = COMMENT.response(user, post);

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.create(eq(post.getId()), eq(user.getUsername()), any(CommentRequest.class))).willReturn(commentDetailResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + post.getId() + "/comments")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.id").value(commentDetailResponse.getId()))
                .andExpect(jsonPath("$.result.comment").value(commentDetailResponse.getComment()))
                .andExpect(jsonPath("$.result.userName").value(commentDetailResponse.getUserName()))
                .andExpect(jsonPath("$.result.postId").value(commentDetailResponse.getPostId()));

        verify(commentService).create(eq(post.getId()), eq(user.getUsername()), any(CommentRequest.class));
    }

    @Test
    @DisplayName("작성 : 실패 - 해당 게시글 없음")
    void create_post_not_found() throws Exception {
        final User user = USER.init();
        final Post post = POST.init(user);
        final CommentRequest commentRequest = COMMENT.createRequest();

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.create(eq(post.getId()), eq(user.getUsername()), any(CommentRequest.class))).willThrow(new PostNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + post.getId() + "/comments")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().is(POST_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("작성 : 실패 - 로그인하지 않은 경우, 잘못된 토큰")
    void create_invalid_token() throws Exception {
        final User user = USER.init();
        final Post post = POST.init(user);
        final CommentRequest commentRequest = COMMENT.createRequest();

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + post.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().is(INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("조회 : 정상")
    void findByPostId() throws Exception {
        final User user = USER.init();
        final Post post = POST.init(user);
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<CommentDetailResponse> commentDetailResponsePage = new PageImpl<>(List.of(
                CommentDetailResponse.of(COMMENT.init(1L, user, post)),
                CommentDetailResponse.of(COMMENT.init(2L, user, post)),
                CommentDetailResponse.of(COMMENT.init(3L, user, post))
        ));

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.findByPost(eq(post.getId()), any(Pageable.class))).willReturn(commentDetailResponsePage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/" + post.getId() + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.pageable").exists())
                .andExpect(jsonPath("$.result.size").value(3));
    }
}