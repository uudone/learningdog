package com.learningdog.media.api;

import com.learningdog.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@RequestMapping("mediaProcess")
public class MediaProcessController {

    @Autowired
    private MediaProcessService  mediaProcessService;
}
