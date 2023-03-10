package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
import com.likelion.mutsasns.dto.comment.CommentRequest;
import com.likelion.mutsasns.exception.AbstractBaseException;
import com.likelion.mutsasns.exception.notfound.CommentNotFoundException;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
import com.likelion.mutsasns.repository.CommentRepository;
import com.likelion.mutsasns.repository.PostRepository;
import com.likelion.mutsasns.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static com.likelion.mutsasns.exception.ErrorCode.*;
import static com.likelion.mutsasns.support.fixture.CommentFixture.COMMENT;
import static com.likelion.mutsasns.support.fixture.PostFixture.POST;
import static com.likelion.mutsasns.support.fixture.UserFixture.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class CommentServiceTest {
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private final ApplicationEventPublisher publisher = Mockito.mock(ApplicationEventPublisher.class);
    private final CommentService commentService = new CommentService(commentRepository, userRepository, postRepository, publisher);

    @Test
    @DisplayName("?????? : ??????")
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
    @DisplayName("?????? : ?????? - ?????? ?????? ??????")
    void create_user_not_found() {
        final User user = USER.init();
        final Post post = POST.init(user);

        when(userRepository.findByUsername(anyString())).thenThrow(new UserNotFoundException());

        AbstractBaseException e = assertThrows(UserNotFoundException.class, () -> commentService.create(post.getId(), user.getUsername(), COMMENT.createRequest()));
        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ????????? ??????")
    void create_post_not_found() {
        final User user = USER.init();
        final Post post = POST.init(user);

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenThrow(new PostNotFoundException());

        AbstractBaseException e = assertThrows(PostNotFoundException.class, () -> commentService.create(post.getId(), user.getUsername(), COMMENT.createRequest()));
        assertEquals(POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ??????")
    void update() {
        final User user = USER.init();
        final Post post = POST.init();
        final Comment comment = COMMENT.init();
        final CommentRequest updateRequest = COMMENT.updateRequest();

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(commentRepository.findByIdAndDeletedDateTimeIsNull(comment.getId())).willReturn(Optional.of(comment));

        CommentDetailResponse updateResponse = commentService.update(post.getId(), comment.getId(), user.getUsername(), updateRequest);

        assertEquals(comment.getId(), updateResponse.getId());
        assertEquals(comment.getPostId(), updateResponse.getPostId());
        assertEquals(comment.getUser().getUsername(), updateResponse.getUserName());
        assertEquals(updateRequest.getComment(), updateResponse.getComment());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ?????? ??????")
    void update_user_not_found() {
        when(userRepository.findByUsername(anyString())).thenThrow(new UserNotFoundException());

        AbstractBaseException e = assertThrows(UserNotFoundException.class, () -> commentService.update(1L, 1L, "user1", COMMENT.updateRequest()));
        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ????????? ??????")
    void update_post_not_found() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(USER.init()));
        when(postRepository.findById(anyLong())).thenThrow(new PostNotFoundException());

        AbstractBaseException e = assertThrows(PostNotFoundException.class, () -> commentService.update(1L, 1L, "user1", COMMENT.updateRequest()));
        assertEquals(POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ?????? ??????")
    void update_comment_not_found() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(USER.init()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(POST.init()));
        when(commentRepository.findByIdAndDeletedDateTimeIsNull(anyLong())).thenThrow(new CommentNotFoundException());

        AbstractBaseException e = assertThrows(CommentNotFoundException.class, () -> commentService.update(1L, 1L, "user1", COMMENT.updateRequest()));
        assertEquals(COMMENT_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ??????")
    void update_invalid_permission() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(OTHER_USER.init()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(POST.init()));
        given(commentRepository.findByIdAndDeletedDateTimeIsNull(anyLong())).willReturn(Optional.of(COMMENT.init()));

        AbstractBaseException e = assertThrows(InvalidPermissionException.class, () -> commentService.update(1L, 1L, "other_user", COMMENT.updateRequest()));
        assertEquals(INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ?????? - ????????? ??????")
    void update_admin() {
        final User admin = ADMIN.init();
        final Post post = POST.init();
        final Comment comment = COMMENT.init();
        final CommentRequest updateRequest = COMMENT.updateRequest();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(admin));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(commentRepository.findByIdAndDeletedDateTimeIsNull(anyLong())).willReturn(Optional.of(comment));

        CommentDetailResponse updateResponse = commentService.update(post.getId(), comment.getId(), admin.getUsername(), updateRequest);

        assertEquals(comment.getId(), updateResponse.getId());
        assertEquals(comment.getPostId(), updateResponse.getPostId());
        assertEquals(comment.getUser().getUsername(), updateResponse.getUserName());
        assertEquals(updateRequest.getComment(), updateResponse.getComment());
    }

    @Test
    @DisplayName("?????? : ??????")
    void delete() {
        final User user = USER.init();
        final Post post = POST.init();
        final Comment comment = COMMENT.init();

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(commentRepository.findByIdAndDeletedDateTimeIsNull(comment.getId())).willReturn(Optional.of(comment));

        Long deletedId = commentService.delete(post.getId(), comment.getId(), user.getUsername());

        assertEquals(comment.getId(), deletedId);
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ?????? ??????")
    void delete_user_not_found() {
        when(userRepository.findByUsername(anyString())).thenThrow(new UserNotFoundException());

        AbstractBaseException e = assertThrows(UserNotFoundException.class, () -> commentService.delete(1L, 1L, "user1"));
        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ????????? ??????")
    void delete_post_not_found() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(USER.init()));
        when(postRepository.findById(anyLong())).thenThrow(new PostNotFoundException());

        AbstractBaseException e = assertThrows(PostNotFoundException.class, () -> commentService.delete(1L, 1L, "user1"));
        assertEquals(POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ?????? ??????")
    void delete_comment_not_found() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(USER.init()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(POST.init()));
        when(commentRepository.findByIdAndDeletedDateTimeIsNull(anyLong())).thenThrow(new CommentNotFoundException());

        AbstractBaseException e = assertThrows(CommentNotFoundException.class, () -> commentService.delete(1L, 1L, "user1"));
        assertEquals(COMMENT_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ?????? - ?????? ??????")
    void delete_invalid_permission() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(OTHER_USER.init()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(POST.init()));
        given(commentRepository.findByIdAndDeletedDateTimeIsNull(anyLong())).willReturn(Optional.of(COMMENT.init()));

        AbstractBaseException e = assertThrows(InvalidPermissionException.class, () -> commentService.delete(1L, 1L, "other_user"));
        assertEquals(INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    @DisplayName("?????? : ?????? - ????????? ??????")
    void delete_admin() {
        final User admin = ADMIN.init();
        final Post post = POST.init();
        final Comment comment = COMMENT.init();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(admin));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(commentRepository.findByIdAndDeletedDateTimeIsNull(anyLong())).willReturn(Optional.of(comment));

        Long deletedId = commentService.delete(post.getId(), comment.getId(), admin.getUsername());

        assertEquals(comment.getId(), deletedId);
    }
}