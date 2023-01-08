package com.likelion.mutsasns.controller;

import com.likelion.mutsasns.dto.ResultResponse;
import com.likelion.mutsasns.dto.user.*;
import com.likelion.mutsasns.service.UserService;
import com.likelion.mutsasns.support.annotation.Login;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "회원")
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "로그인")
    @PostMapping("/login")
    public ResultResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("로그인 id : {}", loginRequest.getUserName());
        LoginResponse response = userService.login(loginRequest);
        return ResultResponse.success(response);
    }

    @ApiOperation(value = "회원가입")
    @PostMapping("/join")
    public ResultResponse<JoinResponse> join(@RequestBody JoinRequest joinRequest) {
        log.info("회원가입 id : {}", joinRequest.getUserName());
        JoinResponse response = userService.join(joinRequest);
        return ResultResponse.success(response);
    }

    @Login
    @ApiOperation(value = "회원 권한 변경")
    @PostMapping("/{id}/role/change")
    public ResultResponse<UserDetailResponse> updateUserRole(@ApiIgnore Principal principal, @PathVariable Long id, @RequestBody UpdateUserRoleRequest updateUserRoleRequest) {
        log.info("유저 권한 변경 userId:{}", id);
        UserDetailResponse response = userService.updateRole(principal.getName(), id, updateUserRoleRequest);
        return ResultResponse.success(response);
    }
}
