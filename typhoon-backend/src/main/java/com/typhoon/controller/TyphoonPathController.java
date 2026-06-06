package com.typhoon.controller;

import com.typhoon.entity.TyphoonPath;
import com.typhoon.service.TyphoonPathService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/typhoon/path")
@CrossOrigin
public class TyphoonPathController {

    private final TyphoonPathService typhoonPathService;

    public TyphoonPathController(TyphoonPathService typhoonPathService) {
        this.typhoonPathService = typhoonPathService;
    }

    @GetMapping("/{id}")
    public List<TyphoonPath> getPath(@PathVariable Long id) {

        return typhoonPathService.getPathByTyphoonId(id);
    }
}