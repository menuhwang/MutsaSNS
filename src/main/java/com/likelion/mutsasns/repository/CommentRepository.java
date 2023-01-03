package com.likelion.mutsasns.repository;

import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost(Post post, Pageable pageable);
}
