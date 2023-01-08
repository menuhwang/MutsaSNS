package com.likelion.mutsasns.domain.post;

import com.likelion.mutsasns.domain.BaseEntity;
import com.likelion.mutsasns.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    private int likes;

    @ManyToOne(optional = false)
    private User user;

    @Builder
    public Post(Long id, String title, String body, User user) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
    }

    public void update(Post update) {
        this.title = update.getTitle();
        this.body = update.getBody();
    }

    public void delete() {
        this.deletedDateTime = LocalDateTime.now();
    }

    public synchronized void likes() {
        likes++;
    }

    public synchronized void unlikes() {
        if (likes > 0) likes--;
    }

    public boolean equalUser(User user) {
        return this.user.getId().equals(user.getId());
    }
}
