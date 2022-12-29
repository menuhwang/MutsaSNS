package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostDetailResponse;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
import com.likelion.mutsasns.repository.PostRepository;
import com.likelion.mutsasns.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
    @DisplayName("작성 : 정상")
    void create() {
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));
        when(postRepository.save(any(Post.class))).thenReturn(POST);

        Long result = postService.create(USERNAME, POST_REQUEST);

        assertEquals(POST_ID, result);
    }

    @Test
    @DisplayName("작성 : 실패 - 해당 유저 없음")
    void create_user_not_found() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.create(USERNAME, POST_REQUEST));
    }

    @Test
    @DisplayName("상세 조회 : 정상")
    void findById() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(POST));

        PostDetailResponse result = postService.findById(POST_ID);

        assertEquals(POST_ID, result.getId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(BODY, result.getBody());
        assertEquals(USERNAME, result.getUserName());
    }

    @Test
    @DisplayName("상세 조회 : 실패 - 해당 게시글 없음")
    void findById_post_not_found() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findById(POST_ID));
    }

    @Test
    @DisplayName("수정 : 정상")
    void update() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));

        Long result = postService.update(USERNAME, POST_ID, UPDATE_REQUEST);

        assertEquals(POST_ID, result);
    }

    @Test
    @DisplayName("수정 : 실패 - 해당 게시물 없음")
    void update_post_not_found() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.update(USERNAME, POST_ID, UPDATE_REQUEST));
    }

    @Test
    @DisplayName("수정 : 실패 - 해당 유저 없음")
    void update_user_not_found() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.update(USERNAME, POST_ID, UPDATE_REQUEST));
    }

    @Test
    @DisplayName("수정 : 실패 - 접근 권한 없음")
    void update_user_not_accessible() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        given(userRepository.findByUsername(OTHER_USERNAME)).willReturn(Optional.of(OTHER_USER));

        assertThrows(InvalidPermissionException.class, () -> postService.update(OTHER_USERNAME, POST_ID, UPDATE_REQUEST));
    }

    @Test
    @DisplayName("삭제 : 정상")
    void deleteById() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));

        Long result = postService.deleteById(USERNAME, POST_ID);

        assertEquals(POST_ID, result);
    }

    @Test
    @DisplayName("삭제 : 실패 - 해당 유저 없음")
    void deleteById_user_not_found() {
        given(postRepository.findById(POST_ID)).willReturn(Optional.of(POST));
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> postService.deleteById(USERNAME, POST_ID));
    }

    @Test
    @DisplayName("삭제 : 실패 - 해당 게시물 없음")
    void deleteById_post_not_found() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deleteById(USERNAME, POST_ID));
    }
}