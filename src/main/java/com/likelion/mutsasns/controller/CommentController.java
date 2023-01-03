package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.ResultResponse;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
import com.likelion.mutsasns.dto.comment.CommentRequest;
import com.likelion.mutsasns.service.CommentService;
import com.likelion.mutsasns.support.annotation.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class CommentController {
    private final CommentService commentService;

    @Login
    @PostMapping("/{postId}/comments")
    public ResultResponse<CommentDetailResponse> create(@ApiIgnore Principal principal, @PathVariable Long postId, @RequestBody CommentRequest commentRequest) {
        return ResultResponse.success(commentService.create(postId, principal.getName(), commentRequest));
    }
}
