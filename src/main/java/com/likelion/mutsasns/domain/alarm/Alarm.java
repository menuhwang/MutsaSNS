package com.likelion.mutsasns.domain.alarm;

import com.likelion.mutsasns.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;
    @ManyToOne
    private User fromUser;
    @ManyToOne
    private User targetUser;
    @CreatedDate
    private LocalDateTime createdDateTime;

    @Builder
    public Alarm(AlarmType alarmType, User fromUser, User targetUser) {
        this.alarmType = alarmType;
        this.fromUser = fromUser;
        this.targetUser = targetUser;
    }
}