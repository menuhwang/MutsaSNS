package com.likelion.mutsasns.security.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.dto.ErrorResponse;
import com.likelion.mutsasns.exception.unauthorized.InvalidTokenException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorResponse errorResponse = new ErrorResponse(new InvalidTokenException());

        response.setStatus(403);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
