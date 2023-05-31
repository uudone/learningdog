package com.learningdog.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learningdog.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author getjiajia
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    /**
     * @param shardTotal:
     * @param shardIndex:
     * @param count:
     * @return List<MediaProcess>
     * @author getjiajia
     * @description 根据分片参数获取待处理任务
     */
    @Select("select * from media_process t where t.id%#{shardTotal}=#{shardIndex} and (t.status='1' or t.status='3') limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal")int shardTotal,@Param("shardIndex")int shardIndex,@Param("count")int count);

    @Update("update media_process t set t.status='4' where (t.status='1' or t.status='3') and t.fail_count<3 and t.id=#{id}")
    int startTask(@Param("id") long id);


}
