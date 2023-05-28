package com.learningdog.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.media.model.dto.QueryMediaParamsDto;
import com.learningdog.media.model.po.MediaFiles;

/**
 * <p>
 * 媒资信息 服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-05-27
 */
public interface MediaFilesService extends IService<MediaFiles> {

    /**
     * @param companyId:
     * @param params:
     * @param queryMediaParamsDto:
     * @return PageResult<MediaFiles>
     * @author getjiajia
     * @description 分页查询符合条件的媒资文件
     */
    PageResult<MediaFiles> list(Long companyId, PageParams params, QueryMediaParamsDto queryMediaParamsDto);

}
