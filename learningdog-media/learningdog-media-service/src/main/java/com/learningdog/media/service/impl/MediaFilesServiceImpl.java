package com.learningdog.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.media.mapper.MediaFilesMapper;
import com.learningdog.media.model.dto.QueryMediaParamsDto;
import com.learningdog.media.model.po.MediaFiles;
import com.learningdog.media.service.MediaFilesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 媒资信息 服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class MediaFilesServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFilesService {

    @Resource
    MediaFilesService mediaFilesService;

    @Resource
    MediaFilesMapper mediaFilesMapper;

    @Override
    public PageResult<MediaFiles> list(Long companyId, PageParams params, QueryMediaParamsDto queryMediaParamsDto) {
        String filename=queryMediaParamsDto.getFilename();
        String fileType=queryMediaParamsDto.getFileType();
        String auditStatus=queryMediaParamsDto.getAuditStatus();
        //构建查询条件
        LambdaQueryWrapper<MediaFiles> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaFiles::getCompanyId,companyId);
        queryWrapper.eq(StringUtils.isNotEmpty(filename),MediaFiles::getFilename,filename);
        queryWrapper.eq(StringUtils.isNotEmpty(fileType),MediaFiles::getFileType,fileType);
        queryWrapper.eq(StringUtils.isNotEmpty(auditStatus),MediaFiles::getAuditStatus,auditStatus);
        //创建分页参数
        Page<MediaFiles> page=new Page<>(params.getPageNo(),params.getPageSize());
        Page<MediaFiles> result = mediaFilesMapper.selectPage(page, queryWrapper);
        //封装查询结果
        PageResult<MediaFiles> pageResult=new PageResult<>(result.getRecords(),
                                                        result.getTotal(),
                                                        params.getPageNo(),
                                                        params.getPageSize());
        return pageResult;
    }
}
