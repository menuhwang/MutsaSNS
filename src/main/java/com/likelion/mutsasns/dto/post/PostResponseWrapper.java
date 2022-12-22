package com.likelion.mutsasns.dto.post;

import lombok.Getter;

@Getter
public class PostResponseWrapper {
    private String message;
    private Long postId;

    private PostResponseWrapper() {
    }

    public PostResponseWrapper(String message, PostResponse postResponse) {
        this.message = message;
        this.postId = postResponse.getId();
    }

    public PostResponseWrapper(String message, Long id) {
        this.message = message;
        this.postId = id;
    }
}
