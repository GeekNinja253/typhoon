package com.typhoon.controller;

import com.typhoon.common.Result;
import com.typhoon.entity.TyphoonPath;
import com.typhoon.service.TyphoonPathService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/typhoon/path")
@CrossOrigin(origins = "*")
public class TyphoonPathController {

    private final TyphoonPathService typhoonPathService;

    public TyphoonPathController(TyphoonPathService typhoonPathService) {
        this.typhoonPathService = typhoonPathService;
    }

    @GetMapping("/{id}")
    public Result<List<TyphoonPath>> getPath(@PathVariable Long id) {
        return Result.success(typhoonPathService.getPathByTyphoonId(id));
    }
}
