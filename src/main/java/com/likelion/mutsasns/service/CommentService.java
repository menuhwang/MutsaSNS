package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
import com.likelion.mutsasns.dto.comment.CommentRequest;
import com.likelion.mutsasns.exception.badrequest.InvalidUpdateCommentException;
import com.likelion.mutsasns.exception.notfound.CommentNotFoundException;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
import com.likelion.mutsasns.repository.CommentRepository;
import com.likelion.mutsasns.repository.PostRepository;
import com.likelion.mutsasns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentDetailResponse create(Long postId, String username, CommentRequest commentRequest) {
        User user = findUserByUsername(username);
        Post post = findPostById(postId);
        Comment comment = commentRepository.save(commentRequest.toEntity(post, user));
        return CommentDetailResponse.of(comment);
    }

    public Page<CommentDetailResponse> findByPost(Long postId, Pageable pageable) {
        Post post = findPostById(postId);
        return commentRepository.findByPostAndDeletedDateTimeIsNull(post, pageable).map(CommentDetailResponse::of);
    }

    @Transactional
    public CommentDetailResponse update(Long postId, Long id, String username, CommentRequest updateRequest) {
        Comment comment = findTargetComment(postId, id, username);

        comment.update(updateRequest.toEntity());

        return CommentDetailResponse.of(comment);
    }

    @Transactional
    public Long delete(Long postId, Long id, String username) {
        Comment comment = findTargetComment(postId, id, username);

        comment.delete();

        return comment.getId();
    }

    private void verifyAccessibleComment(Comment comment, User user) {
        if (!comment.getUserId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN)
            throw new InvalidPermissionException();
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    private Post findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    private Comment findCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    private void validRequest(Comment comment, Post post) {
        if (!comment.equalPost(post)) throw new InvalidUpdateCommentException();
    }

    private Comment findTargetComment(Long postId, Long id, String username) {
        User user = findUserByUsername(username);
        Post post = findPostById(postId);
        Comment comment = findCommentById(id);

        validRequest(comment, post);

        verifyAccessibleComment(comment, user);

        return comment;
    }
}
