package com.likelion.mutsasns.dto.post;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import lombok.Getter;

@Getter
public class PostRequest {
    private String title;
    private String body;

    private PostRequest() {
    }

    public PostRequest(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .body(body)
                .build();
    }

    public Post toEntity(User user) {
        return Post.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();
    }
}
