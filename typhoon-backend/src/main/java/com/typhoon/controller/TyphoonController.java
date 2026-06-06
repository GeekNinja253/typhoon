package com.typhoon.controller;

import com.typhoon.entity.Typhoon;
import com.typhoon.service.TyphoonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/typhoon")
public class TyphoonController {

    @Autowired
    private TyphoonService typhoonService;

    @GetMapping("/list")
    public List<Typhoon> list() {
        return typhoonService.list();
    }
}