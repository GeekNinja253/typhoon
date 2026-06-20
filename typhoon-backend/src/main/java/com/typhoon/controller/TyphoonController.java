package com.typhoon.controller;

import com.typhoon.common.Result;
import com.typhoon.entity.Typhoon;
import com.typhoon.service.TyphoonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/typhoon")
@CrossOrigin(origins = "*")
public class TyphoonController {

    @Autowired
    private TyphoonService typhoonService;

    @GetMapping("/list")
    public Result<List<Typhoon>> list() {
        return Result.success(typhoonService.list());
    }
}
