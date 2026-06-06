package com.typhoon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.typhoon.entity.TyphoonPath;
import com.typhoon.mapper.TyphoonPathMapper;
import com.typhoon.service.TyphoonPathService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TyphoonPathServiceImpl implements TyphoonPathService {

    private final TyphoonPathMapper typhoonPathMapper;

    public TyphoonPathServiceImpl(TyphoonPathMapper typhoonPathMapper) {
        this.typhoonPathMapper = typhoonPathMapper;
    }

    @Override
    public List<TyphoonPath> getPathByTyphoonId(Long typhoonId) {

        QueryWrapper<TyphoonPath> wrapper = new QueryWrapper<>();

        wrapper.eq("typhoon_id", typhoonId);

        return typhoonPathMapper.selectList(wrapper);
    }
}