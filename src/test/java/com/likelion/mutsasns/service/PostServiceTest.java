package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostResponse;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
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
    private final Long OTHER_USER_ID = 2L;
    private final String USERNAME = "tester";
    private final String OTHER_USERNAME = "other";
    private final String PASSWORD = "password";
    private final User USER = User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .password(PASSWORD)
            .build();
    private final User OTHER_USER = User.builder()
            .id(OTHER_USER_ID)
            .username(OTHER_USERNAME)
            .password(PASSWORD)
            .build();
    private final Principal PRINCIPAL = new UsernamePasswordAuthenticationToken(USER, null, null);
    private final Principal OTHER_PRINCIPAL = new UsernamePasswordAuthenticationToken(OTHER_USER, null, null);
    private final Long POST_ID = 1L;
    private final String TITLE = "this is title";
    private final String BODY = "this is body";
    private final String UPDATE_TITLE = "this is update title";
    private final String UPDATE_BODY = "this is update body";
    private final PostRequest POST_REQUEST = new PostRequest(TITLE, BODY);
    private final PostRequest UPDATE_REQUEST = new PostRequest(UPDATE_TITLE, UPDATE_BODY);
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

    @Test
    void findById() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(POST));

        PostResponse result = postService.findById(POST_ID);

        assertEquals(POST_ID, result.getId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(BODY, result.getBody());
        assertEquals(USERNAME, result.getUserName());
    }

    @Test
    void findById_post_not_found() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findById(POST_ID));
    }

    @Test
    void update() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));

        PostResponse result = postService.update(PRINCIPAL, POST_ID, UPDATE_REQUEST);

        assertEquals(UPDATE_TITLE, result.getTitle());
        assertEquals(UPDATE_BODY, result.getBody());
    }

    @Test
    void update_post_not_found() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.update(PRINCIPAL, POST_ID, UPDATE_REQUEST));
    }

    @Test
    void update_user_not_found() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.update(PRINCIPAL, POST_ID, UPDATE_REQUEST));
    }

    @Test
    void update_user_not_accessible() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        given(userRepository.findByUsername(OTHER_USERNAME)).willReturn(Optional.of(OTHER_USER));

        assertThrows(InvalidPermissionException.class, () -> postService.update(OTHER_PRINCIPAL, POST_ID, UPDATE_REQUEST));
    }

    @Test
    void deleteById() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));

        Long result = postService.deleteById(PRINCIPAL, POST_ID);

        assertEquals(POST_ID, result);
    }

    @Test
    void deleteById_user_not_found() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.deleteById(PRINCIPAL, POST_ID));
    }

    @Test
    void deleteById_post_not_found() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deleteById(PRINCIPAL, POST_ID));
    }
}