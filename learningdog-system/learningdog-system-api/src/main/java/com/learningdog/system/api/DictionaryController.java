package com.learningdog.system.api;

import com.learningdog.system.model.po.Dictionary;
import com.learningdog.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    @Resource
    private DictionaryService dictionaryService;

    @GetMapping("/all")
    public List<Dictionary> queryAll(){
        return dictionaryService.queryAll();
    }

    @GetMapping("/code/{code}")
    public Dictionary getByCode(@PathVariable("code")String code){
        return dictionaryService.getByCode(code);
    }
}
