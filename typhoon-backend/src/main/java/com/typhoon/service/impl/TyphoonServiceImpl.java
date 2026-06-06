package com.typhoon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.typhoon.entity.Typhoon;
import com.typhoon.mapper.TyphoonMapper;
import com.typhoon.service.TyphoonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TyphoonServiceImpl implements TyphoonService {

    private final TyphoonMapper typhoonMapper;

    public TyphoonServiceImpl(TyphoonMapper typhoonMapper) {
        this.typhoonMapper = typhoonMapper;
    }

    @Override
    public List<Typhoon> listAll() {
        return typhoonMapper.selectList(null);
    }

    @Override
    public Typhoon getByCode(String typhoonCode) {
        QueryWrapper<Typhoon> wrapper = new QueryWrapper<>();
        wrapper.eq("typhoon_code", typhoonCode);
        return typhoonMapper.selectOne(wrapper);
    }

    @Override
    public List<Typhoon> list() {
        return typhoonMapper.selectList(null);
    }

}