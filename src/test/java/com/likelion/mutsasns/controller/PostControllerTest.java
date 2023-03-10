package com.likelion.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostDetailResponse;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
import com.likelion.mutsasns.security.provider.JwtProvider;
import com.likelion.mutsasns.service.PostService;
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

import java.util.List;

import static com.likelion.mutsasns.exception.ErrorCode.*;
import static com.likelion.mutsasns.support.TestConstant.*;
import static com.likelion.mutsasns.support.fixture.AuthenticationFixture.AUTHENTICATION;
import static com.likelion.mutsasns.support.fixture.PostFixture.POST;
import static com.likelion.mutsasns.support.fixture.UserFixture.OTHER_USER;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestWithSecurity(controllers = PostController.class)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;
    @MockBean
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("?????? : ??????")
    void create() throws Exception {
        final User user = USER.init();
        final Post post = POST.init(user);

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(postService.create(anyString(), any(PostRequest.class))).willReturn(post.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(POST.createRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                .andExpect(jsonPath("$.result.postId").value(post.getId()));

        verify(postService).create(anyString(), any(PostRequest.class));
    }

    @Test
    @DisplayName("?????? : ?????? - ??????????????? ?????? ??????")
    void create_user_not_logged_in() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(POST.createRequest())))
                .andExpect(status().is(USER_NOT_LOGGED_IN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(USER_NOT_LOGGED_IN.name()))
                .andExpect(jsonPath("$.result.message").value(USER_NOT_LOGGED_IN.getMessage()));

        verify(postService, never()).create(anyString(), any(PostRequest.class));
    }

    @Test
    @DisplayName("?????? : ?????? - ????????? ??????")
    void create_invalid_token() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(POST.createRequest())))
                .andExpect(status().is(INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()));

        verify(postService, never()).create(anyString(), any(PostRequest.class));
    }

    @Test
    @DisplayName("?????? ?????? : ??????")
    void findById() throws Exception {
        final User user = USER.init();
        final Post post = POST.init(user);

        given(postService.findById(post.getId())).willReturn(PostDetailResponse.of(post));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(post.getId()))
                .andExpect(jsonPath("$.result.title").value(post.getTitle()))
                .andExpect(jsonPath("$.result.body").value(post.getBody()))
                .andExpect(jsonPath("$.result.userName").value(post.getUser().getUsername()));

        verify(postService).findById(post.getId());
    }

    @Test
    @DisplayName("?????? ?????? : ?????? - ?????? ????????? ??????")
    void findById_post_not_found() throws Exception {
        given(postService.findById(1L)).willThrow(new PostNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/" + 1))
                .andExpect(status().is(POST_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()));

        verify(postService).findById(1L);
    }

    @Test
    @DisplayName("?????? : ??????")
    void update() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(postService.update(anyString(), anyLong(), any(PostRequest.class))).willReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(POST.updateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                .andExpect(jsonPath("$.result.postId").value(1));

        verify(jwtProvider).validateToken(MOCK_TOKEN);
        verify(jwtProvider).getAuthentication(MOCK_TOKEN);
        verify(postService).update(anyString(), anyLong(), any(PostRequest.class));
    }

    @Test
    @DisplayName("?????? : ?????? - ????????? ??????")
    void update_invalid_token() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(POST.updateRequest())))
                .andExpect(status().is(INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()));

        verify(jwtProvider).validateToken(MOCK_TOKEN);
    }

    @Test
    @DisplayName("?????? : ?????? - ??????????????? ?????? ??????")
    void update_user_not_logged_in() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(POST.updateRequest())))
                .andExpect(status().is(USER_NOT_LOGGED_IN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(USER_NOT_LOGGED_IN.name()))
                .andExpect(jsonPath("$.result.message").value(USER_NOT_LOGGED_IN.getMessage()));

        verify(postService, never()).update(anyString(), anyLong(), any(PostRequest.class));
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ?????? ??????")
    void update_user_not_accessible() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(postService.update(anyString(), anyLong(), any(PostRequest.class))).willThrow(new InvalidPermissionException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(POST.updateRequest())))
                .andExpect(status().is(INVALID_PERMISSION.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_PERMISSION.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_PERMISSION.getMessage()));

        verify(jwtProvider).validateToken(MOCK_TOKEN);
        verify(jwtProvider).getAuthentication(MOCK_TOKEN);
        verify(postService).update(anyString(), anyLong(), any(PostRequest.class));
    }

    @Test
    @DisplayName("?????? : ??????")
    void deleteById() throws Exception {
        final User user = USER.init();
        final Post post = POST.init(user);

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(postService.deleteById(anyString(), anyLong())).willReturn(post.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + post.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                .andExpect(jsonPath("$.result.postId").value(post.getId()));

        verify(jwtProvider).validateToken(MOCK_TOKEN);
        verify(jwtProvider).getAuthentication(MOCK_TOKEN);
        verify(postService).deleteById(anyString(), anyLong());
    }

    @Test
    @DisplayName("?????? : ?????? - ????????? ??????")
    void deleteById_invalid_token() throws Exception {
        final User user = USER.init();
        final Post post = POST.init(user);

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + post.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()));

        verify(jwtProvider).validateToken(MOCK_TOKEN);
        verify(postService, never()).deleteById(anyString(), anyLong());
    }

    @Test
    @DisplayName("?????? : ?????? - ??????????????? ?????? ??????")
    void deleteById_user_not_logged_in() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + 1))
                .andExpect(status().is(USER_NOT_LOGGED_IN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(USER_NOT_LOGGED_IN.name()))
                .andExpect(jsonPath("$.result.message").value(USER_NOT_LOGGED_IN.getMessage()));

        verify(postService, never()).deleteById(anyString(), anyLong());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ????????? ??????")
    void deleteById_post_not_found() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(postService.deleteById(anyString(), anyLong())).willThrow(new PostNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(POST_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()));

        verify(jwtProvider).validateToken(MOCK_TOKEN);
        verify(jwtProvider).getAuthentication(MOCK_TOKEN);
        verify(postService).deleteById(anyString(), anyLong());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ?????? ??????")
    void deleteById_user_not_accessible() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(postService.deleteById(anyString(), anyLong())).willThrow(new InvalidPermissionException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/" + 1)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(INVALID_PERMISSION.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_PERMISSION.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_PERMISSION.getMessage()));

        verify(jwtProvider).validateToken(MOCK_TOKEN);
        verify(jwtProvider).getAuthentication(MOCK_TOKEN);
        verify(postService).deleteById(anyString(), anyLong());
    }

    @Test
    @DisplayName("???????????? : ??????")
    void findMyPosts() throws Exception {
        final User user = USER.init();
        final Page<PostDetailResponse> posts = new PageImpl<>(List.of(
                PostDetailResponse.of(POST.init(1L)),
                PostDetailResponse.of(POST.init(2L)),
                PostDetailResponse.of(POST.init(3L))
        ));

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        given(postService.findByUsername(eq(user.getUsername()), any(Pageable.class))).willReturn(posts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/my")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.pageable").exists())
                .andExpect(jsonPath("$.result.size").exists());

        verify(postService).findByUsername(eq(user.getUsername()), any(Pageable.class));
    }

    @Test
    @DisplayName("???????????? : ?????? - ??????????????? ?????? ??????")
    void findMyPosts_user_not_logged_in() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/my"))
                .andExpect(status().is(USER_NOT_LOGGED_IN.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(USER_NOT_LOGGED_IN.name()))
                .andExpect(jsonPath("$.result.message").value(USER_NOT_LOGGED_IN.getMessage()));

        verify(postService, never()).deleteById(anyString(), anyLong());
    }

    @Test
    @DisplayName("????????? : ?????? - ?????????")
    void likes() throws Exception {
        Post post = POST.init(OTHER_USER.init());
        User user = USER.init();

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        when(postService.likes(post.getId(), user.getUsername())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + post.getId() + "/likes")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result").value("???????????? ???????????????."));

        verify(postService).likes(post.getId(), user.getUsername());
    }

    @Test
    @DisplayName("????????? : ?????? - ????????? ??????")
    void likes_unlikes() throws Exception {
        Post post = POST.init(OTHER_USER.init());
        User user = USER.init();

        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        when(postService.likes(post.getId(), user.getUsername())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + post.getId() + "/likes")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result").value("???????????? ??????????????????."));

        verify(postService).likes(post.getId(), user.getUsername());
    }

    @Test
    @DisplayName("????????? : ?????? - ?????? ?????? ??????")
    void likes_user_not_found() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        when(postService.likes(anyLong(), anyString())).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + 1 + "/likes")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(USER_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(USER_NOT_FOUND.getMessage()));

        verify(postService).likes(anyLong(), anyString());
    }

    @Test
    @DisplayName("????????? : ?????? - ?????? ????????? ??????")
    void likes_post_not_found() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION.init());
        when(postService.likes(anyLong(), anyString())).thenThrow(new PostNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/" + 1 + "/likes")
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MOCK_TOKEN))
                .andExpect(status().is(POST_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(POST_NOT_FOUND.getMessage()));

        verify(postService).likes(anyLong(), anyString());
    }
}