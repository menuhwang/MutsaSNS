package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.ResultResponse;
import com.likelion.mutsasns.dto.comment.CommentDetailResponse;
import com.likelion.mutsasns.dto.comment.CommentRequest;
import com.likelion.mutsasns.dto.comment.CommentResultResponse;
import com.likelion.mutsasns.service.CommentService;
import com.likelion.mutsasns.support.annotation.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/{postId}/comments")
    public ResultResponse<Page<CommentDetailResponse>> findByPostId(@PathVariable Long postId, @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResultResponse.success(commentService.findByPost(postId, pageable));
    }

    @Login
    @PutMapping("/{postId}/comments/{id}")
    public ResultResponse<CommentDetailResponse> update(@ApiIgnore Principal principal, @PathVariable Long postId, @PathVariable Long id, @RequestBody CommentRequest updateRequest) {
        return ResultResponse.success(commentService.update(postId, id, principal.getName(), updateRequest));
    }

    @Login
    @DeleteMapping("/{postId}/comments/{id}")
    public ResultResponse<CommentResultResponse> delete(@ApiIgnore Principal principal, @PathVariable Long postId, @PathVariable Long id) {
        Long deleteId = commentService.delete(postId, id, principal.getName());
        return ResultResponse.success(new CommentResultResponse("댓글 삭제 완료", deleteId));
    }
}
