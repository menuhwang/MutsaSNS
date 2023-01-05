package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.Role;
import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.post.PostDetailResponse;
import com.likelion.mutsasns.dto.post.PostRequest;
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
        User user = findUserByUsername(username);
        Post post = postRepository.save(postRequest.toEntity(user));
        return post.getId();
    }

    public PostDetailResponse findById(Long id) {
        return PostDetailResponse.of(findPostById(id));
    }

    @Transactional
    public Long update(String username, Long id, PostRequest updateRequest) {
        Post post = findTargetPost(username, id);
        post.update(updateRequest.toEntity());
        return post.getId();
    }

    @Transactional
    public Long deleteById(String username, Long id) {
        Post post = findTargetPost(username, id);
        post.delete();
        return post.getId();
    }

    public Page<PostDetailResponse> findAll(Pageable pageable) {
        return postRepository.findByDeletedDateTimeIsNull(pageable).map(PostDetailResponse::of);
    }

    private Post findPostById(Long id) {
        return postRepository.findByIdAndDeletedDateTimeIsNull(id).orElseThrow(PostNotFoundException::new);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    private Post findTargetPost(String username, Long postId) {
        Post post = findPostById(postId);
        User user = findUserByUsername(username);
        verifyAccessiblePost(post, user);
        return post;
    }

    private void verifyAccessiblePost(Post post, User user) {
        if (!post.equalUser(user) && user.getRole() != Role.ROLE_ADMIN) throw new InvalidPermissionException();
    }
}
