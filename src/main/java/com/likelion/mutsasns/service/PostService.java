package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostDetailResponse;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
import com.likelion.mutsasns.repository.PostRepository;
import com.likelion.mutsasns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Long create(String username, PostRequest postRequest) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.save(postRequest.toEntity(user));
        return post.getId();
    }

    public PostDetailResponse findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        return PostDetailResponse.of(post);
    }

    @Transactional
    public Long update(String username, Long id, PostRequest updateRequest) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (isNotAccessiblePost(post, user)) throw new InvalidPermissionException();
        post.update(updateRequest.toEntity());
        return post.getId();
    }

    public Long deleteById(String username, Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (isNotAccessiblePost(post, user)) throw new InvalidPermissionException();
        postRepository.deleteById(id);
        return post.getId();
    }

    public Page<PostDetailResponse> findAll(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostDetailResponse::of);
    }

    public boolean isNotAccessiblePost(Post post, User user) {
        return !post.getUserId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN;
    }
}
