package com.likelion.mutsasns.repository;

import com.likelion.mutsasns.domain.alarm.Alarm;
import com.likelion.mutsasns.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Page<Alarm> findByTargetUser(User user, Pageable pageable);
}
