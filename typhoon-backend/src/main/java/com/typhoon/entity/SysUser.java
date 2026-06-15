package com.typhoon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class SysUser {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String role;

    private LocalDateTime createTime;
}