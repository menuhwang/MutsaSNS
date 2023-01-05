package com.likelion.mutsasns.repository;

import com.likelion.mutsasns.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndDeletedDateTimeIsNull(Long id);
    Page<Post> findByDeletedDateTimeIsNull(Pageable pageable);
}
