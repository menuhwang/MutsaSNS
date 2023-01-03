package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
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
import static com.likelion.mutsasns.support.fixture.CommentFixture.COMMENT;
import static com.likelion.mutsasns.support.fixture.PostFixture.POST;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class CommentServiceTest {
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private final CommentService commentService = new CommentService(commentRepository, userRepository, postRepository);

    @Test
    @DisplayName("작성 : 정상")
    void create() {
        final User user = USER.init();
        final Post post = POST.init(user);
        final Comment comment = COMMENT.init(user, post);

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDetailResponse result = commentService.create(post.getId(), user.getUsername(), COMMENT.createRequest());

        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getComment(), result.getComment());
        assertEquals(user.getUsername(), result.getUserName());
        assertEquals(post.getId(), result.getPostId());
    }

    @Test
    @DisplayName("작성 : 실패 - 해당 유저 없음")
    void create_user_not_found() {
        final User user = USER.init();
        final Post post = POST.init(user);

        when(userRepository.findByUsername(anyString())).thenThrow(new UserNotFoundException());

        AbstractBaseException e = assertThrows(UserNotFoundException.class, () -> commentService.create(post.getId(), user.getUsername(), COMMENT.createRequest()));
        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("작성 : 실패 - 해당 게시물 없음")
    void create_post_not_found() {
        final User user = USER.init();
        final Post post = POST.init(user);

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenThrow(new PostNotFoundException());

        AbstractBaseException e = assertThrows(PostNotFoundException.class, () -> commentService.create(post.getId(), user.getUsername(), COMMENT.createRequest()));
        assertEquals(POST_NOT_FOUND, e.getErrorCode());
    }
}