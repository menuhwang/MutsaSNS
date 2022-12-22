package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.SuccessResponse;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostResponse;
import com.likelion.mutsasns.dto.post.PostResponseWrapper;
import com.likelion.mutsasns.service.PostService;
import com.likelion.mutsasns.support.annotation.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("")
    public Page<PostResponse> findAll(@PageableDefault(size = 20, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.findAll(pageable);
    }

    @Login
    @PostMapping("")
    public SuccessResponse<PostResponseWrapper> create(@ApiIgnore Principal principal, @RequestBody PostRequest postRequest) {
        log.info("포스트 작성 title:{}, body:{}", postRequest.getTitle(), postRequest.getBody());
        PostResponse response = postService.create(principal, postRequest);
        return new SuccessResponse<>(response.toWrapperDTO("포스트 등록 완료"));
    }

    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable Long id) {
        log.info("포스트 상세조회 id:{}", id);
        return postService.findById(id);
    }

    @Login
    @PutMapping("/{id}")
    public SuccessResponse<PostResponseWrapper> update(Principal principal, @PathVariable Long id, @RequestBody PostRequest updateRequest) {
        log.info("포스트 수정 id:{}, title:{}, body:{}", id, updateRequest.getTitle(), updateRequest.getBody());
        PostResponse response = postService.update(principal, id, updateRequest);
        return new SuccessResponse<>(response.toWrapperDTO("포스트 수정 완료"));
    }

    @Login
    @DeleteMapping("/{id}")
    public SuccessResponse<PostResponseWrapper> deleteById(Principal principal, @PathVariable Long id) {
        log.info("포스트 삭제 id:{}", id);
        Long deletedId = postService.deleteById(principal, id);
        return new SuccessResponse<>(new PostResponseWrapper("포스트 삭제 완료", deletedId));
    }
}
