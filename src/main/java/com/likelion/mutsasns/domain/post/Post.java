package com.likelion.mutsasns.domain.post;

import com.likelion.mutsasns.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Post {
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

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastModifiedDateTime;
    private LocalDateTime deletedDateTime;

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

    public void likes() {
        likes++;
    }

    public void unlikes() {
        if (likes > 0) likes--;
    }

    public boolean equalUser(User user) {
        return this.user.getId().equals(user.getId());
    }
}
