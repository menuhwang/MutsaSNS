package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
import com.likelion.mutsasns.dto.comment.CommentRequest;
import com.likelion.mutsasns.exception.AbstractBaseException;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.repository.CommentRepository;
import com.likelion.mutsasns.repository.PostRepository;
import com.likelion.mutsasns.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.likelion.mutsasns.exception.ErrorCode.POST_NOT_FOUND;
import static com.likelion.mutsasns.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class CommentServiceTest {
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private final CommentService commentService = new CommentService(commentRepository, userRepository, postRepository);

    private final Long USER_ID = 1L;
    private final String USERNAME = "tester";
    private final String PASSWORD = "password";
    private final User USER = User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .password(PASSWORD)
            .build();
    private final Long POST_ID = 1L;
    private final String TITLE = "this is title";
    private final String BODY = "this is body";
    private final Post POST = Post.builder()
            .id(POST_ID)
            .title(TITLE)
            .body(BODY)
            .user(USER)
            .build();
    private final Long COMMENT_ID = 1L;
    private final String COMMENT_CONTENT = "content";
    private final CommentRequest COMMENT_REQUEST = new CommentRequest(COMMENT_CONTENT);
    private final Comment COMMENT = Comment.builder()
            .id(COMMENT_ID)
            .comment(COMMENT_CONTENT)
            .user(USER)
            .post(POST)
            .build();

    @Test
    @DisplayName("작성 : 정상")
    void create() {
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));
        given(postRepository.findById(USER_ID)).willReturn(Optional.of(POST));
        when(commentRepository.save(any(Comment.class))).thenReturn(COMMENT);

        CommentDetailResponse result = commentService.create(POST_ID, USERNAME, COMMENT_REQUEST);

        assertEquals(COMMENT_ID, result.getId());
        assertEquals(COMMENT_CONTENT, result.getComment());
        assertEquals(USERNAME, result.getUserName());
        assertEquals(POST_ID, result.getPostId());
    }

    @Test
    @DisplayName("작성 : 실패 - 해당 유저 없음")
    void create_user_not_found() {
        when(userRepository.findByUsername(USERNAME)).thenThrow(new UserNotFoundException());

        AbstractBaseException e = assertThrows(UserNotFoundException.class, () -> commentService.create(POST_ID, USERNAME, COMMENT_REQUEST));
        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("작성 : 실패 - 해당 게시물 없음")
    void create_post_not_found() {
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(USER));
        when(postRepository.findById(POST_ID)).thenThrow(new PostNotFoundException());

        AbstractBaseException e = assertThrows(PostNotFoundException.class, () -> commentService.create(POST_ID, USERNAME, COMMENT_REQUEST));
        assertEquals(POST_NOT_FOUND, e.getErrorCode());
    }
}