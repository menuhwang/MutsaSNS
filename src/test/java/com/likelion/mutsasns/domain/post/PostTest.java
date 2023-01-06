package com.likelion.mutsasns.domain.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Test
    @DisplayName("좋아요 : 동시성 테스트")
    void like_multi_thread() throws InterruptedException {
        /* // 동시성 테스트용 비즈니스 로직 Post.likes()
        try {
            int temp = likes;
            Thread.sleep(100);
            likes = temp + 1;
        }catch (InterruptedException e) {
        }
        */
        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        Post post = POST.init();

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                post.likes();
                latch.countDown();
            });
        }
        latch.await();
        assertEquals(numberOfThreads, post.getLikes());
    }
}