package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.security.provider.JwtProvider;
import com.likelion.mutsasns.service.HelloService;
import com.likelion.mutsasns.support.annotation.WebMvcTestWithSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestWithSecurity(controllers = HelloController.class)
@MockBean(JpaMetamodelMappingContext.class)
class HelloControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HelloService helloService;
    @MockBean
    private JwtProvider jwtProvider;

    @Test
    void sumOfDigit() throws Exception {
        int NUM = 1234;
        int SUM = 10;
        given(helloService.sumOfDigit(NUM)).willReturn(SUM);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/hello/" + NUM))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(SUM)));

        verify(helloService).sumOfDigit(NUM);
    }
}