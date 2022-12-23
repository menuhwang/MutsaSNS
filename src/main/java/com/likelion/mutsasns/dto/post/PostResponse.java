package com.likelion.mutsasns.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.mutsasns.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String body;
    private String userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModifiedAt;

    private PostResponse() {
    }

    @Builder
    public PostResponse(Long id, String title, String body, String userName, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userName = userName;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    public static PostResponse of(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUsername())
                .createdAt(post.getCreatedDateTime())
                .lastModifiedAt(post.getLastModifiedDateTime())
                .build();
    }

    public PostResponseWrapper toWrapperDTO(String message) {
        return new PostResponseWrapper(message, this);
    }
}
