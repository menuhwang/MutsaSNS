package com.likelion.mutsasns.service;

import com.likelion.mutsasns.domain.user.User;
import com.likelion.mutsasns.dto.alarm.AlarmResponse;
import com.likelion.mutsasns.exception.notfound.UserNotFoundException;
import com.likelion.mutsasns.repository.AlarmRepository;
import com.likelion.mutsasns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    public Page<AlarmResponse> findMyAlarms(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return alarmRepository.findByTargetUser(user, pageable).map(AlarmResponse::of);
    }
}
