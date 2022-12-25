package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.SuccessResponse;
import com.likelion.mutsasns.dto.user.*;
import com.likelion.mutsasns.service.UserService;
import com.likelion.mutsasns.support.annotation.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public SuccessResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("로그인 id : {}", loginRequest.getUserName());
        LoginResponse response = userService.login(loginRequest);
        return new SuccessResponse<>(response);
    }

    @PostMapping("/join")
    public SuccessResponse<JoinResponse> join(@RequestBody JoinRequest joinRequest) {
        log.info("회원가입 id : {}", joinRequest.getUserName());
        JoinResponse response = userService.join(joinRequest);
        return new SuccessResponse<>(response);
    }

    @Login
    @PostMapping("/{id}/role/change")
    public SuccessResponse<UserDetailResponse> updateUserRole(Principal principal, @PathVariable Long id, @RequestBody UpdateUserRoleRequest updateUserRoleRequest) {
        log.info("유저 권한 변경 userId:{}", id);
        UserDetailResponse response = userService.updateRole(principal.getName(), id, updateUserRoleRequest);
        return new SuccessResponse<>(response);
    }
}
