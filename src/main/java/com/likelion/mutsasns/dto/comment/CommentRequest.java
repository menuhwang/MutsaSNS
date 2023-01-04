package com.likelion.mutsasns.dto.comment;

import com.likelion.mutsasns.domain.comment.Comment;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import lombok.Getter;

@Getter
public class CommentRequest {
    private String comment;

    private CommentRequest() {
    }

    public CommentRequest(String comment) {
        this.comment = comment;
    }

    public Comment toEntity(Post post, User user) {
        return Comment.builder()
                .comment(comment)
                .post(post)
                .user(user)
                .build();
    }

    public Comment toEntity() {
        return Comment.builder()
                .comment(comment)
                .build();
    }
}
