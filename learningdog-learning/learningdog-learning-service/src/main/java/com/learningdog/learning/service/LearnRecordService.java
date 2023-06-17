package com.learningdog.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.base.model.RestResponse;
import com.learningdog.learning.po.LearnRecord;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-06-12
 */
public interface LearnRecordService extends IService<LearnRecord> {

    /**
     * @param userId:
     * @param courseId:
     * @param teachplanId:
     * @param mediaId:
     * @return RestResponse<String>
     * @author getjiajia
     * @description 已登录用户获取教学视频
     */
    RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);

    /**
     * @param courseId:
     * @param teachplanId:
     * @param mediaId:
     * @return RestResponse<String>
     * @author getjiajia
     * @description 未登录用户获取免费教学视频
     */
    RestResponse<String> getVideoFree(Long courseId, Long teachplanId, String mediaId);
}
