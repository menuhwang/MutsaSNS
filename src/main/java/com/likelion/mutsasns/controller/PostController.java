package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.ResultResponse;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostDetailResponse;
import com.likelion.mutsasns.dto.post.PostResultResponse;
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
    public ResultResponse<Page<PostDetailResponse>> findAll(@PageableDefault(size = 20, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDetailResponse> responses = postService.findAll(pageable);
        return ResultResponse.success(responses);
    }

    @Login
    @PostMapping("")
    public ResultResponse<PostResultResponse> create(@ApiIgnore Principal principal, @RequestBody PostRequest postRequest) {
        log.info("포스트 작성 title:{}, body:{}", postRequest.getTitle(), postRequest.getBody());
        Long createdId = postService.create(principal.getName(), postRequest);
        return ResultResponse.success(new PostResultResponse("포스트 등록 완료", createdId));
    }

    @Login
    @GetMapping("/my")
    public ResultResponse<Page<PostDetailResponse>> findMyPosts(@ApiIgnore Principal principal, @PageableDefault(size = 20, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("마이 피드 조회 user:{}", principal.getName());
        return ResultResponse.success(postService.findByUsername(principal.getName(), pageable));
    }

    @GetMapping("/{id}")
    public ResultResponse<PostDetailResponse> findById(@PathVariable Long id) {
        log.info("포스트 상세조회 id:{}", id);
        return ResultResponse.success(postService.findById(id));
    }

    @Login
    @PutMapping("/{id}")
    public ResultResponse<PostResultResponse> update(@ApiIgnore Principal principal, @PathVariable Long id, @RequestBody PostRequest updateRequest) {
        log.info("포스트 수정 id:{}, title:{}, body:{}", id, updateRequest.getTitle(), updateRequest.getBody());
        Long updatedId = postService.update(principal.getName(), id, updateRequest);
        return ResultResponse.success(new PostResultResponse("포스트 수정 완료", updatedId));
    }

    @Login
    @DeleteMapping("/{id}")
    public ResultResponse<PostResultResponse> deleteById(@ApiIgnore Principal principal, @PathVariable Long id) {
        log.info("포스트 삭제 id:{}", id);
        Long deletedId = postService.deleteById(principal.getName(), id);
        return ResultResponse.success(new PostResultResponse("포스트 삭제 완료", deletedId));
    }

    @Login
    @PostMapping("/{id}/likes")
    public ResultResponse<String> likes(@ApiIgnore Principal principal, @PathVariable Long id) {
        log.info("포스트 좋아요 id:{}", id);
        boolean result = postService.likes(id, principal.getName());
        return ResultResponse.success(result ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.");
    }

    @GetMapping("/{id}/likes")
    public ResultResponse<Integer> getLikes(@PathVariable Long id) {
        log.info("포스트 좋아요 개수 조회 id:{}", id);
        return ResultResponse.success(postService.findById(id).getLikes());
    }
}
