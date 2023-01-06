package com.likelion.mutsasns.domain.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.likelion.mutsasns.support.fixture.PostFixture.POST;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PostTest {
    @Test
    @DisplayName("좋아요 : 정상")
    void likes() {
        final Post post = POST.init();

        post.likes();

        assertEquals(1, post.getLikes());
    }

    @Test
    @DisplayName("좋아요 취소 : 정상")
    void unlike() {
        final Post post = POST.init();

        post.likes();

        post.unlikes();

        assertEquals(0, post.getLikes());
    }

    @Test
    @DisplayName("좋아요 취소 : 좋아요가 0 이하인 경우")
    void unlike_likes_is_less_than_equal_to_zero() {
        final Post post = POST.init();

        post.unlikes();

        assertEquals(0, post.getLikes());
    }
}