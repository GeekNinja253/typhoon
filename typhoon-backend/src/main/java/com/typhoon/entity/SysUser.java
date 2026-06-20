package com.typhoon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

/**
 * 系统用户实体
 * 包含用户信息的校验规则
 */
@Data
@TableName("users")
public class SysUser {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
    private String password;

    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    private String role;

    private LocalDateTime createTime;
}
