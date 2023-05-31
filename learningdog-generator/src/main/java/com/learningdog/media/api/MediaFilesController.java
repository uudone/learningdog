package com.learningdog.media.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.learningdog.media.service.MediaFilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 媒资信息 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@RequestMapping("mediaFiles")
public class MediaFilesController {

    @Autowired
    private MediaFilesService  mediaFilesService;
}