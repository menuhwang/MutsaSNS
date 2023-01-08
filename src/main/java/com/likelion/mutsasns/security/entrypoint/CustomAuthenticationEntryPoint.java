package com.likelion.mutsasns.security.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.dto.ErrorResponse;
import com.likelion.mutsasns.dto.ResultResponse;
import com.likelion.mutsasns.exception.AbstractBaseException;
import com.likelion.mutsasns.exception.unauthorized.InvalidTokenException;
import com.likelion.mutsasns.exception.unauthorized.UserNotLoggedInException;
import org.springframework.http.HttpStatus;
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
        boolean existsToken = (boolean) request.getAttribute("existsToken");

        ObjectMapper objectMapper = new ObjectMapper();

        AbstractBaseException e = existsToken ? new InvalidTokenException() : new UserNotLoggedInException();

        ResultResponse<ErrorResponse> resultResponse = ResultResponse.error(e);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(resultResponse));
    }
}
