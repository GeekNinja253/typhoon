package com.typhoon.controller;

import com.typhoon.entity.SysUser;
import com.typhoon.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.typhoon.common.Result;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService userService;

    @PostMapping("/login")
    public Result<SysUser> login(
            @RequestBody SysUser user
    ){

        SysUser loginUser =
                userService.login(
                        user.getUsername(),
                        user.getPassword()
                );

        return Result.success(
                "登录成功",
                loginUser
        );
    }

    @PostMapping("/register")
    public Result<String> register(
            @RequestBody SysUser user
    ){

        userService.register(user);

        return Result.success(
                "注册成功",
                null
        );
    }
}
