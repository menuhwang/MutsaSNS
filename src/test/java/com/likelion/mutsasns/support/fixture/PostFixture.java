package com.likelion.mutsasns.support.fixture;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostRequest;

import static com.likelion.mutsasns.support.fixture.UserFixture.USER;

public enum PostFixture {
    POST(1L, "title", "body", USER.init());

    private final Long id;
    private final String title;
    private final String body;
    private final User user;

    PostFixture(Long id, String title, String body, User user) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
    }

    public Post init() {
        return Post.builder()
                .id(id)
                .title(title)
                .body(body)
                .user(user)
                .build();
    }

    public Post init(User user) {
        return Post.builder()
                .id(id)
                .title(title)
                .body(body)
                .user(user)
                .build();
    }

    public Post init(Long id) {
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
