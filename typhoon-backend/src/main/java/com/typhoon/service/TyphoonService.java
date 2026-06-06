package com.typhoon.service;

import com.typhoon.entity.Typhoon;
import java.util.List;

public interface TyphoonService {

    // 查询所有台风
    List<Typhoon> listAll();

    // 根据台风编号查询
    Typhoon getByCode(String typhoonCode);

    List<Typhoon> list();
}