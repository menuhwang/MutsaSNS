package com.likelion.mutsasns.dto.alarm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.mutsasns.domain.alarm.Alarm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlarmResponse {
    private Long id;
    private String fromUser;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private AlarmResponse() {
    }

    @Builder
    public AlarmResponse(Long id, String fromUser, String message, LocalDateTime createdAt) {
        this.id = id;
        this.fromUser = fromUser;
        this.message = message;
        this.createdAt = createdAt;
    }

    public static AlarmResponse of(Alarm alarm) {
        return AlarmResponse.builder()
                .id(alarm.getId())
                .fromUser(alarm.getFromUser().getUsername())
                .message(alarm.getAlarmType().getMessage())
                .createdAt(alarm.getCreatedDateTime())
                .build();
    }
}
