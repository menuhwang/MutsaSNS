package com.likelion.mutsasns.repository;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.post.PostLike;
import com.likelion.mutsasns.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
}
