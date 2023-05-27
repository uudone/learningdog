package com.learningdog.media.service.impl;

import com.learningdog.media.model.po.MediaFiles;
import com.learningdog.media.mapper.MediaFilesMapper;
import com.learningdog.media.service.MediaFilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

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

}
