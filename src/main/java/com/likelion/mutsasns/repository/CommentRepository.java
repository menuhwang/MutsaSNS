package com.likelion.mutsasns.repository;

import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndDeletedDateTimeIsNull(Long id);
    Page<Comment> findByPostAndDeletedDateTimeIsNull(Post post, Pageable pageable);
}
