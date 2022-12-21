package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostRequest;
import com.likelion.mutsasns.dto.post.PostResponse;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.repository.PostRepository;
import com.likelion.mutsasns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
