package com.likelion.mutsasns.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.mutsasns.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDetailResponse {
    private Long id;
    private String title;
    private String body;
    private String userName;
    private int likes;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModifiedAt;

    private PostDetailResponse() {
    }

    @Builder
    public PostDetailResponse(Long id, String title, String body, String userName, int likes, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userName = userName;
        this.likes = likes;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    public static PostDetailResponse of(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUsername())
                .likes(post.getLikes())
                .createdAt(post.getCreatedDateTime())
                .lastModifiedAt(post.getLastModifiedDateTime())
                .build();
    }
}
