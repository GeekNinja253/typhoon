package com.typhoon.controller;

import com.typhoon.entity.SysUser;
import com.typhoon.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.typhoon.common.Result;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private SysUserService userService;

    @PostMapping("/login")
    public Result<SysUser> login(
            @Valid @RequestBody SysUser user
    ) {
        SysUser loginUser = userService.login(user.getUsername(), user.getPassword());
        return Result.success("登录成功", loginUser);
    }

    @PostMapping("/register")
    public Result<String> register(
            @Valid @RequestBody SysUser user
    ) {
        userService.register(user);
        return Result.success("注册成功", null);
    }
}
