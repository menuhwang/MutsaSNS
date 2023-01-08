package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.service.HelloService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = "CI/CD 테스트")
@RequestMapping("/api/v1/hello")
public class HelloController {
    private final HelloService helloService;
    private final String KEYWORD = "황민우";
    @ApiOperation(value = "Keyword 반환")
    @GetMapping(value = "", produces = "text/plain;charset=utf-8")
    public String hello() {
        return KEYWORD;
    }

    @ApiOperation(value = "자릿수 합산")
    @GetMapping(value = "/{num}")
    public Integer sumOfDigit(@PathVariable Integer num) {
        return helloService.sumOfDigit(num);
    }
}
