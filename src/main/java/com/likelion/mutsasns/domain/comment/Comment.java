package com.likelion.mutsasns.domain.comment;

import com.likelion.mutsasns.domain.BaseEntity;
import com.likelion.mutsasns.domain.post.Post;
import com.likelion.mutsasns.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 300)
    private String comment;
    @ManyToOne
    private User user;
    @ManyToOne
    private Post post;

    @Builder
    public Comment(Long id, String comment, LocalDateTime createdDateTime, LocalDateTime lastModifiedDateTime, User user, Post post) {
        this.id = id;
        this.comment = comment;
        this.createdDateTime = createdDateTime;
        this.lastModifiedDateTime = lastModifiedDateTime;
        this.user = user;
        this.post = post;
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getPostId() {
        return post.getId();
    }

    public void update(Comment update) {
        this.comment = update.getComment();
    }

    public void delete() {
        this.deletedDateTime = LocalDateTime.now();
    }

    public boolean equalPost(Post post) {
        return this.post.getId().equals(post.getId());
    }
}
