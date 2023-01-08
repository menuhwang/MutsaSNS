package com.likelion.mutsasns.observer.events;

import com.likelion.mutsasns.domain.alarm.Alarm;
import com.likelion.mutsasns.domain.alarm.AlarmType;
import com.likelion.mutsasns.domain.user.User;

public class AlarmEvent {
    private final Alarm alarm;

    private AlarmEvent(Alarm alarm) {
        this.alarm = alarm;
    }

    public static AlarmEvent of(AlarmType alarmType, User target, User from) {
        return new AlarmEvent(Alarm.builder()
                .alarmType(alarmType)
                .targetUser(target)
                .fromUser(from)
                .build());
    }

    public Alarm getAlarm() {
        return alarm;
    }
}
