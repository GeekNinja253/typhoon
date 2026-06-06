package com.typhoon.controller;

import com.typhoon.entity.SysUser;
import com.typhoon.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService userService;

    @PostMapping("/login")
    public SysUser login(
            @RequestBody SysUser user
    ){

        return userService.login(
                user.getUsername(),
                user.getPassword()
        );
    }

    @PostMapping("/register")
    public String register(
            @RequestBody SysUser user
    ){

        userService.register(user);

        return "注册成功";
    }
}
