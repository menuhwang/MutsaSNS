package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.ResultResponse;
import com.likelion.mutsasns.dto.alarm.AlarmResponse;
import com.likelion.mutsasns.service.AlarmService;
import com.likelion.mutsasns.support.annotation.Login;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Api(tags = "알람")
@RequestMapping("/api/v1/alarms")
public class AlarmController {
    private final AlarmService alarmService;

    @Login
    @ApiOperation(value = "내게 온 알람 조회")
    @GetMapping("")
    public ResultResponse<Page<AlarmResponse>> findMyAlarms(@ApiIgnore Principal principal, Pageable pageable) {
        Page<AlarmResponse> alarmResponses = alarmService.findMyAlarms(principal.getName(), pageable);
        return ResultResponse.success(alarmResponses);
    }
}
