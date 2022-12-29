package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hello")
public class HelloController {
    private final HelloService helloService;
    private final String KEYWORD = "황민우";
    @GetMapping(value = "", produces = "text/plain;charset=utf-8")
    public String hello() {
        return KEYWORD;
    }

    @GetMapping(value = "/{num}")
    public Integer sumOfDigit(@PathVariable Integer num) {
        return helloService.sumOfDigit(num);
    }
}
