package com.learningdog.media.api;

import com.learningdog.base.exception.LearningdogException;
import com.learningdog.base.model.RestResponse;
import com.learningdog.media.po.MediaFiles;
import com.learningdog.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 媒资文件公开接口
 * @version: 1.0
 */
@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {
    @Resource
    MediaFilesService mediaFilesService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId")String mediaId){
        MediaFiles mediaFiles=mediaFilesService.getMediaFiles(mediaId);
        if (mediaFiles==null){
            LearningdogException.cast("文件不存在");
        }
        if (StringUtils.isEmpty(mediaFiles.getUrl())){
            LearningdogException.cast("视频还没有进行转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }

}
