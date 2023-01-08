package com.likelion.mutsasns.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(nullable = false)
    protected LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime lastModifiedDateTime;

    protected LocalDateTime deletedDateTime;
}
