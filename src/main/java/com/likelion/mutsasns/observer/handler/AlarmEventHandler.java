package com.likelion.mutsasns.observer.handler;

import com.likelion.mutsasns.observer.events.AlarmEvent;
import com.likelion.mutsasns.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmEventHandler {
    private final AlarmRepository alarmRepository;

    @EventListener
    public void createAlarm(AlarmEvent e) {
        alarmRepository.save(e.getAlarm());
    }
}
