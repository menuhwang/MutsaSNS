package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostDetailResponse;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.exception.AbstractBaseException;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
import com.likelion.mutsasns.repository.PostRepository;
import com.likelion.mutsasns.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.likelion.mutsasns.exception.ErrorCode.*;
import static com.likelion.mutsasns.support.fixture.PostFixture.POST;
import static com.likelion.mutsasns.support.fixture.UserFixture.OTHER_USER;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class PostServiceTest {
    private final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PostService postService = new PostService(postRepository, userRepository);

    @Test
    @DisplayName("작성 : 정상")
    void create() {
        final User user = USER.init();
        final Post post = POST.init(user);
        final PostRequest postCreateRequest = POST.createRequest();

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long result = postService.create(user.getUsername(), postCreateRequest);

        assertEquals(post.getId(), result);
    }

    @Test
    @DisplayName("작성 : 실패 - 해당 유저 없음")
    void create_user_not_found() {
        final User user = USER.init();
        final PostRequest postCreateRequest = POST.createRequest();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        AbstractBaseException e = assertThrows(UserNotFoundException.class, () -> postService.create(user.getUsername(), postCreateRequest));
        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("상세 조회 : 정상")
    void findById() {
        final User user = USER.init();
        final Post post = POST.init(user);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        PostDetailResponse result = postService.findById(post.getId());

        assertEquals(post.getId(), result.getId());
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getBody(), result.getBody());
        assertEquals(post.getUser().getUsername(), result.getUserName());
    }

    @Test
    @DisplayName("상세 조회 : 실패 - 해당 게시글 없음")
    void findById_post_not_found() {
        final User user = USER.init();
        final Post post = POST.init(user);

        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        AbstractBaseException e = assertThrows(PostNotFoundException.class, () -> postService.findById(post.getId()));
        assertEquals(POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("수정 : 정상")
    void update() {
        final User user = USER.init();
        final Post post = POST.init(user);
        final PostRequest postUpdateRequest = POST.updateRequest();

        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        Long result = postService.update(user.getUsername(), post.getId(), postUpdateRequest);

        assertEquals(post.getId(), result);
    }

    @Test
    @DisplayName("수정 : 실패 - 해당 게시물 없음")
    void update_post_not_found() {
        final User user = USER.init();
        final Post post = POST.init(user);
        final PostRequest postUpdateRequest = POST.updateRequest();

        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        AbstractBaseException e = assertThrows(PostNotFoundException.class, () -> postService.update(user.getUsername(), post.getId(), postUpdateRequest));
        assertEquals(POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("수정 : 실패 - 해당 유저 없음")
    void update_user_not_found() {
        final User user = USER.init();
        final Post post = POST.init(user);
        final PostRequest postUpdateRequest = POST.updateRequest();

        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        AbstractBaseException e = assertThrows(UserNotFoundException.class, () -> postService.update(user.getUsername(), post.getId(), postUpdateRequest));
        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("수정 : 실패 - 접근 권한 없음")
    void update_user_not_accessible() {
        final User user = USER.init();
        final User otherUser = OTHER_USER.init();
        final Post post = POST.init(otherUser);
        final PostRequest postUpdateRequest = POST.updateRequest();

        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        AbstractBaseException e = assertThrows(InvalidPermissionException.class, () -> postService.update(user.getUsername(), post.getId(), postUpdateRequest));
        assertEquals(INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    @DisplayName("삭제 : 정상")
    void deleteById() {
        final User user = USER.init();
        final Post post = POST.init(user);

        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        Long result = postService.deleteById(user.getUsername(), post.getId());

        assertEquals(post.getId(), result);
    }

    @Test
    @DisplayName("삭제 : 실패 - 해당 유저 없음")
    void deleteById_user_not_found() {
        final User user = USER.init();
        final Post post = POST.init(user);

        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        AbstractBaseException e = assertThrows(UserNotFoundException.class, () -> postService.deleteById(user.getUsername(), post.getId()));
        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("삭제 : 실패 - 해당 게시물 없음")
    void deleteById_post_not_found() {
        final User user = USER.init();
        final Post post = POST.init(user);

        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        AbstractBaseException e = assertThrows(PostNotFoundException.class, () -> postService.deleteById(user.getUsername(), post.getId()));
        assertEquals(POST_NOT_FOUND, e.getErrorCode());
    }
}