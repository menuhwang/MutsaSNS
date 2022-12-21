package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostResponse;
import com.likelion.mutsasns.exception.notfound.PostNotFoundException;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
import com.likelion.mutsasns.repository.PostRepository;
import com.likelion.mutsasns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse create(Principal principal, PostRequest postRequest) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.save(postRequest.toEntity(user));
        return PostResponse.of(post);
    }

    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        return PostResponse.of(post);
    }

    @Transactional
    public PostResponse update(Principal principal, Long id, PostRequest updateRequest) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        if (isNotAccessiblePost(post, user)) throw new InvalidPermissionException();
        post.update(updateRequest.toEntity());
        return PostResponse.of(post);
    }

    public boolean isNotAccessiblePost(Post post, User user) {
        return !post.getUserId().equals(user.getId()) && user.getRole() != Role.ADMIN;
    }
}
