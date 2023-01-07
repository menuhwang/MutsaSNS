package com.likelion.mutsasns.repository;

import com.likelion.mutsasns.domain.alarm.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
