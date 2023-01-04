package com.likelion.mutsasns.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.mutsasns.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDetailResponse {
    private Long id;
    private String comment;
    private String userName;
    private Long postId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private CommentDetailResponse() {
    }

    @Builder
    public CommentDetailResponse(Long id, String comment, String userName, Long postId, LocalDateTime createdAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public static CommentDetailResponse of(Comment comment) {
        return CommentDetailResponse.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUsername())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedDateTime())
                .build();
    }
}
