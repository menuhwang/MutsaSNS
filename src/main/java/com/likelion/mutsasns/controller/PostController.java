package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.SuccessResponse;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostResponse;
import com.likelion.mutsasns.dto.post.PostResponseWrapper;
import com.likelion.mutsasns.service.PostService;
import com.likelion.mutsasns.support.annotation.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @Login
    @PostMapping("")
    public SuccessResponse<PostResponseWrapper> create(@ApiIgnore Principal principal, @RequestBody PostRequest postRequest) {
        log.info("포스트 작성 title:{}, body:{}", postRequest.getTitle(), postRequest.getBody());
        PostResponse response = postService.create(principal, postRequest);
        return new SuccessResponse<>(response.toWrapperDTO("포스트 등록 완료"));
    }

    @GetMapping("{id}")
    public PostResponse findById(@PathVariable Long id) {
        log.info("포스트 상세조회 id:{}", id);
        return postService.findById(id);
    }
}
