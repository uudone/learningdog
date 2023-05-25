package com.learningdog.system.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.learningdog.system.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@RequestMapping("dictionary")
public class DictionaryController {

    @Autowired
    private DictionaryService  dictionaryService;
}
