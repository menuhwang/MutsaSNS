package com.likelion.mutsasns.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloServiceTest {
    private final HelloService helloService = new HelloService();

    @Test
    @DisplayName("자릿수 합 : 정상")
    void sumOfDigit() {
        int NUM = 1234;
        int EXPECTED = 10;

        int result = helloService.sumOfDigit(NUM);

        assertEquals(EXPECTED, result);
    }
}