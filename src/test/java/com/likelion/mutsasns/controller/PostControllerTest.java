package com.likelion.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostResponse;
import com.likelion.mutsasns.security.provider.JwtProvider;
import com.likelion.mutsasns.service.PostService;
import com.likelion.mutsasns.support.annotation.WebMvcTestWithSecurity;
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

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestWithSecurity(controllers = PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;
    @MockBean
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();
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
    private final String TITLE = "this is title";
    private final String BODY = "this is body";
    private final PostRequest POST_REQUEST = new PostRequest(TITLE, BODY);
    private final PostResponse POST_RESPONSE = PostResponse.builder()
            .id(POST_ID)
            .title(TITLE)
            .body(BODY)
            .userName(USERNAME)
            .build();
    @Test
    void create() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(true);
        given(jwtProvider.getAuthentication(MOCK_TOKEN)).willReturn(AUTHENTICATION);
        given(postService.create(any(Principal.class), any(PostRequest.class))).willReturn(POST_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + MOCK_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(POST_REQUEST)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result.message").value("포스트 등록 완료"))
            .andExpect(jsonPath("$.result.postId").value(POST_ID));

        verify(postService).create(any(Principal.class), any(PostRequest.class));
    }

    @Test
    void create_no_token_header() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(POST_REQUEST)))
                .andExpect(status().isForbidden());

        verify(postService, never()).create(any(Principal.class), any(PostRequest.class));
    }

    @Test
    void create_invalid_token() throws Exception {
        given(jwtProvider.validateToken(MOCK_TOKEN)).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(POST_REQUEST)))
                .andExpect(status().isForbidden());

        verify(postService, never()).create(any(Principal.class), any(PostRequest.class));
    }
}