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
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        Comment comment = commentRepository.save(commentRequest.toEntity(post, user));
        return CommentDetailResponse.of(comment);
    }

    public Page<CommentDetailResponse> findByPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        return commentRepository.findByPost(post, pageable).map(CommentDetailResponse::of);
    }

    @Transactional
    public CommentDetailResponse update(Long postId, Long id, String username, CommentRequest updateRequest) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        if (!comment.getPostId().equals(postId)) throw new InvalidUpdateCommentException();

        if (isNotAccessibleComment(comment, user)) throw new InvalidPermissionException();

        comment.update(updateRequest.toEntity());

        return CommentDetailResponse.of(comment);
    }

    @Transactional
    public Long delete(Long postId, Long id, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        if (!comment.getPostId().equals(postId)) throw new InvalidUpdateCommentException();

        if (isNotAccessibleComment(comment, user)) throw new InvalidPermissionException();

        comment.delete();

        return comment.getId();
    }

    private boolean isNotAccessibleComment(Comment comment, User user) {
        return !comment.getUserId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN;
    }
}
