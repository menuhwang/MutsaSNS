package com.likelion.mutsasns.support.fixture;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostRequest;

public enum PostFixture {
    POST(1L, "title", "body");

    private final Long id;
    private final String title;
    private final String body;

    PostFixture(Long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public Post init(User user) {
        return Post.builder()
                .id(id)
                .title(title)
                .body(body)
                .user(user)
                .build();
    }

    public PostRequest createRequest() {
        return new PostRequest(title, body);
    }

    public PostRequest updateRequest() {
        return new PostRequest("update-" + title, "update-" + body);
    }
}
