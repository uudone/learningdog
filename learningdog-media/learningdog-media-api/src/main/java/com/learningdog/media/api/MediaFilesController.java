package com.learningdog.media.api;

import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.media.model.dto.QueryMediaParamsDto;
import com.learningdog.media.model.po.MediaFiles;
import com.learningdog.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 媒资信息 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RequestMapping("/files")
public class MediaFilesController {

    @Resource
    private MediaFilesService  mediaFilesService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping
    public PageResult<MediaFiles> list(PageParams params,@RequestBody QueryMediaParamsDto queryMediaParamsDto){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return mediaFilesService.list(companyId,params,queryMediaParamsDto);
    }

}
