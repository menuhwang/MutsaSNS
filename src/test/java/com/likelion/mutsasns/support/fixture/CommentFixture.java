package com.likelion.mutsasns.support.fixture;

import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
import com.likelion.mutsasns.dto.comment.CommentRequest;

public enum CommentFixture {
    COMMENT(1L, "content");

    private final Long id;
    private final String comment;

    CommentFixture(Long id, String comment) {
        this.id = id;
        this.comment = comment;
    }

    public Comment init(User user, Post post) {
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

    public CommentDetailResponse response(User user, Post post) {
        return CommentDetailResponse.builder()
                .id(id)
                .comment(comment)
                .userName(user.getUsername())
                .postId(post.getId())
                .build();
    }
}
