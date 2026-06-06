package com.typhoon.service;

import com.typhoon.entity.TyphoonPath;

import java.util.List;

public interface TyphoonPathService {

    List<TyphoonPath> getPathByTyphoonId(Long typhoonId);

}