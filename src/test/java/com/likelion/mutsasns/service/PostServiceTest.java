package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostResponse;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.repository.PostRepository;
import com.likelion.mutsasns.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class PostServiceTest {
    private final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PostService postService = new PostService(postRepository, userRepository);

    private final Long USER_ID = 1L;
    private final String USERNAME = "tester";
    private final String PASSWORD = "password";
    private final User USER = User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .password(PASSWORD)
            .build();
    private final Principal PRINCIPAL = new UsernamePasswordAuthenticationToken(USER, null, null);
    private final Long POST_ID = 1L;
    private final String TITLE = "this is title";
    private final String BODY = "this is body";
    private final PostRequest POST_REQUEST = new PostRequest(TITLE, BODY);
    private final Post POST = Post.builder()
            .id(POST_ID)
            .title(TITLE)
            .body(BODY)
            .user(USER)
            .build();

    @Test
    void create() {
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));
        when(postRepository.save(any(Post.class))).thenReturn(POST);

        PostResponse result = postService.create(PRINCIPAL, POST_REQUEST);

        assertEquals(POST_ID, result.getId());
        assertEquals(USERNAME, result.getUserName());
        assertEquals(TITLE, result.getTitle());
        assertEquals(BODY, result.getBody());
    }

    @Test
    void create_user_not_found() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.create(PRINCIPAL, POST_REQUEST));
    }
}