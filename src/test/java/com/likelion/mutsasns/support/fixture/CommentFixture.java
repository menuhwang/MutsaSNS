package com.likelion.mutsasns.support.fixture;

import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
import com.likelion.mutsasns.dto.comment.CommentRequest;

import static com.likelion.mutsasns.support.fixture.PostFixture.POST;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;

public enum CommentFixture {
    COMMENT(1L, "content", USER.init(), POST.init());

    private final Long id;
    private final String comment;
    private final User user;
    private final Post post;

    CommentFixture(Long id, String comment, User user, Post post) {
        this.id = id;
        this.comment = comment;
        this.user = user;
        this.post = post;
    }

    public Comment init() {
        return Comment.builder()
                .id(id)
                .comment(comment)
                .user(user)
                .post(post)
                .build();
    }

    public Comment init(User user, Post post) {
        return Comment.builder()
                .id(id)
                .comment(comment)
                .user(user)
                .post(post)
                .build();
    }

    public Comment init(Long id, User user, Post post) {
        return Comment.builder()
                .id(id)
                .comment(comment)
                .user(user)
                .post(post)
                .build();
    }

    public CommentRequest createRequest() {
        return new CommentRequest(comment);
    }

    public CommentDetailResponse response() {
        return CommentDetailResponse.builder()
                .id(id)
                .comment(comment)
                .userName(user.getUsername())
                .postId(post.getId())
                .build();
    }

    public CommentRequest updateRequest() {
        return new CommentRequest("update-" + comment);
    }

    public Comment update(CommentRequest update) {
        return Comment.builder()
                .id(id)
                .comment(update.getComment())
                .user(user)
                .post(post)
                .build();
    }
}
