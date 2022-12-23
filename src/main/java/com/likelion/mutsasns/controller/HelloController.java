package com.likelion.mutsasns.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {
    private final String KEYWORD = "popin";
    @GetMapping("")
    public String hello() {
        return KEYWORD;
    }
}
