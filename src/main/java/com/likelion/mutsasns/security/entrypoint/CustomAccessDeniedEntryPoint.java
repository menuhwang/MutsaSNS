package com.likelion.mutsasns.security.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.mutsasns.dto.ErrorResponse;
import com.likelion.mutsasns.dto.ResultResponse;
import com.likelion.mutsasns.exception.unauthorized.InvalidPermissionException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedEntryPoint implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ResultResponse<ErrorResponse> resultResponse = ResultResponse.error(new InvalidPermissionException());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(resultResponse));
    }
}
