package com.typhoon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.typhoon.entity.SysUser;
import com.typhoon.mapper.SysUserMapper;
import com.typhoon.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public SysUser login(String username, String password) {

        QueryWrapper<SysUser> wrapper =
                new QueryWrapper<>();

        wrapper.eq("username",username);
        wrapper.eq("password",password);

        return userMapper.selectOne(wrapper);
    }

    @Override
    public boolean register(SysUser user) {

        userMapper.insert(user);

        return true;
    }
}