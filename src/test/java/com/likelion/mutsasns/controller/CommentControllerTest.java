package com.likelion.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
import com.likelion.mutsasns.dto.comment.CommentRequest;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
import com.likelion.mutsasns.security.provider.JwtProvider;
import com.likelion.mutsasns.service.CommentService;
import com.likelion.mutsasns.support.annotation.WebMvcTestWithSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.PersistenceException;
import java.util.List;

import static com.likelion.mutsasns.exception.ErrorCode.*;
import static com.likelion.mutsasns.exception.ErrorCode.INVALID_PERMISSION;
import static com.likelion.mutsasns.support.TestConstant.*;
import static com.likelion.mutsasns.support.fixture.AuthenticationFixture.AUTHENTICATION;
import static com.likelion.mutsasns.support.fixture.CommentFixture.COMMENT;
import static com.likelion.mutsasns.support.fixture.PostFixture.POST;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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
        final Post post = POST.init();
        final User user = post.getUser();
        final CommentRequest commentRequest = COMMENT.createRequest();
        final CommentDetailResponse commentDetailResponse = COMMENT.response();

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
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.create(anyLong(), anyString(), any(CommentRequest.class))).willThrow(new PostNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + 1 + "/comments")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(COMMENT.createRequest())))
                .andExpect(status().is(POST_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()));

        verify(commentService).create(anyLong(), anyString(), any(CommentRequest.class));
    }

    @Test
    @DisplayName("작성 : 실패 - 로그인하지 않은 경우, 잘못된 토큰")
    void create_invalid_token() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + 1 + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(COMMENT.createRequest())))
                .andExpect(status().is(INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()));

        verify(commentService, never()).create(anyLong(), anyString(), any(CommentRequest.class));
    }

    @Test
    @DisplayName("조회 : 정상")
    void findByPostId() throws Exception {
        final Post post = POST.init();
        final User user = post.getUser();
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
                .andExpect(jsonPath("$.result.size").exists());

        verify(commentService).findByPost(eq(post.getId()), any(Pageable.class));
    }

    @Test
    @DisplayName("수정 : 정상")
    void update() throws Exception {
        final Comment comment = COMMENT.init();
        final User user = comment.getUser();
        final Post post = comment.getPost();
        final CommentRequest updateRequest = COMMENT.updateRequest();
        final Comment update = COMMENT.update(updateRequest);

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.update(eq(post.getId()), eq(user.getId()), eq(user.getUsername()), any(CommentRequest.class))).willReturn(CommentDetailResponse.of(update));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/" + post.getId() + "/comments/" + comment.getId())
                    .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.id").value(comment.getId()))
                .andExpect(jsonPath("$.result.comment").value(updateRequest.getComment()))
                .andExpect(jsonPath("$.result.userName").value(user.getUsername()))
                .andExpect(jsonPath("$.result.postId").value(post.getId()));

        verify(commentService).update(eq(post.getId()), eq(user.getId()), eq(user.getUsername()), any(CommentRequest.class));
    }

    @Test
    @DisplayName("수정 : 실패 - 인증 실패")
    void update_invalid_token() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/" + 1 + "/comments/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(COMMENT.updateRequest())))
                .andExpect(status().is(INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()));

        verify(commentService, never()).update(anyLong(), anyLong(), anyString(), any(CommentRequest.class));
    }

    @Test
    @DisplayName("수정 : 실패 - 게시물이 없는 경우")
    void update_post_not_found() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.update(anyLong(), anyLong(), anyString(), any(CommentRequest.class))).willThrow(new PostNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/" + 1 + "/comments/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(COMMENT.updateRequest())))
                .andExpect(status().is(POST_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()));

        verify(commentService).update(anyLong(), anyLong(), anyString(), any(CommentRequest.class));
    }

    @Test
    @DisplayName("수정 : 실패 - 수정 권한 없음")
    void update_invalid_permission() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.update(anyLong(), anyLong(), anyString(), any(CommentRequest.class))).willThrow(new InvalidPermissionException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/" + 1 + "/comments/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(COMMENT.updateRequest())))
                .andExpect(status().is(INVALID_PERMISSION.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_PERMISSION.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_PERMISSION.getMessage()));

        verify(commentService).update(anyLong(), anyLong(), anyString(), any(CommentRequest.class));
    }

    @Test
    @DisplayName("수정 : 실패 - 데이터베이스 에러")
    void update_database_error() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.update(anyLong(), anyLong(), anyString(), any(CommentRequest.class))).willThrow(new PersistenceException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/" + 1 + "/comments/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(COMMENT.updateRequest())))
                .andExpect(status().is(DATABASE_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(DATABASE_ERROR.name()))
                .andExpect(jsonPath("$.result.message").value(DATABASE_ERROR.getMessage()));

        verify(commentService).update(anyLong(), anyLong(), anyString(), any(CommentRequest.class));
    }

    @Test
    @DisplayName("삭제 : 정상")
    void delete() throws Exception {
        final Comment comment = COMMENT.init();
        final User user = comment.getUser();
        final Post post = comment.getPost();

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.delete(eq(post.getId()), eq(user.getId()), eq(user.getUsername()))).willReturn(comment.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + post.getId() + "/comments/" + comment.getId())
                    .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value("댓글 삭제 완료"))
                .andExpect(jsonPath("$.result.id").value(comment.getId()));

        verify(commentService).delete(eq(post.getId()), eq(user.getId()), eq(user.getUsername()));
    }

    @Test
    @DisplayName("삭제 : 실패 - 인증 실패")
    void delete_invalid_token() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + 1 + "/comments/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()));

        verify(commentService, never()).delete(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("삭제 : 실패 - 게시물이 없는 경우")
    void delete_post_not_found() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.delete(anyLong(), anyLong(), anyString())).willThrow(new PostNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + 1 + "/comments/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(POST_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()));

        verify(commentService).delete(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("삭제 : 실패 - 삭제 권한 없음")
    void delete_invalid_permission() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.delete(anyLong(), anyLong(), anyString())).willThrow(new InvalidPermissionException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + 1 + "/comments/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(INVALID_PERMISSION.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_PERMISSION.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_PERMISSION.getMessage()));

        verify(commentService).delete(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("삭제 : 실패 - 데이터베이스 에러")
    void delete_database_error() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(commentService.delete(anyLong(), anyLong(), anyString())).willThrow(new PersistenceException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + 1 + "/comments/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(DATABASE_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(DATABASE_ERROR.name()))
                .andExpect(jsonPath("$.result.message").value(DATABASE_ERROR.getMessage()));

        verify(commentService).delete(anyLong(), anyLong(), anyString());
    }
}