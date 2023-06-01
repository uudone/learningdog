package com.learningdog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.content.model.dto.BindTeachplanMediaDto;
import com.learningdog.content.po.TeachplanMedia;
import com.learningdog.media.po.MediaFiles;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-05-24
 */
public interface TeachplanMediaService extends IService<TeachplanMedia> {

    /**
     * @param bindTeachplanMediaDto:
     * @return TeachplanMedia
     * @author getjiajia
     * @description 教学计划绑定媒资文件
     */
    TeachplanMedia  associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * @param teachplanId:
     * @param mediaId:
     * @return void
     * @author getjiajia
     * @description 删除教学计划绑定的媒资文件关系
     */
    void deleteAssociationMedia(Long teachplanId, String mediaId);
}
