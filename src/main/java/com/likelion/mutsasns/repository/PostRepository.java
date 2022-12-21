package com.likelion.mutsasns.repository;

import com.likelion.mutsasns.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
