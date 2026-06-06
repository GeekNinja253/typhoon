package com.typhoon.service;

import com.typhoon.entity.SysUser;

public interface SysUserService {

    SysUser login(String username, String password);

    boolean register(SysUser user);
}
