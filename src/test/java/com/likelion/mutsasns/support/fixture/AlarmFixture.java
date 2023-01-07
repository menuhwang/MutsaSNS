package com.likelion.mutsasns.support.fixture;

import com.likelion.mutsasns.domain.alarm.Alarm;
import com.likelion.mutsasns.domain.alarm.AlarmType;

import static com.likelion.mutsasns.support.fixture.UserFixture.OTHER_USER;
import static com.likelion.mutsasns.support.fixture.UserFixture.USER;

public enum AlarmFixture {
    ALARM;

    public Alarm init(Long id, AlarmType alarmType) {
        return Alarm.builder()
                .id(id)
                .alarmType(alarmType)
                .fromUser(OTHER_USER.init())
                .targetUser(USER.init())
                .build();
    }
}
