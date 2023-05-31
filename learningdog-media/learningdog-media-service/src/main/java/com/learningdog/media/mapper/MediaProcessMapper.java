package com.learningdog.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learningdog.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
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

    /**
     * @param id:
     * @return int
     * @author getjiajia
     * @description 修改字段状态为正在进行
     */
    @Update("update media_process t set t.status='4',t.create_date=#{localDateTime} where (t.status='1' or t.status='3') and t.fail_count<3 and t.id=#{id}")
    int startTask(@Param("id") long id,@Param("localDateTime")LocalDateTime localDateTime);

    /**
     * @param timeout:超时时间，单位为秒
     * @return int
     * @author getjiajia
     * @description 设置正在进行任务的超时时间，将状态修改为失败,并且失败次数加一
     */
    @Update("update media_process t set t.status='3',t.fail_count=t.fail_count+1 where t.status='4' and TIME_TO_SEC(timediff( #{localDateTime}, t.create_date))>#{timeout}")
    int setProcessTimeout(@Param("timeout")long timeout, @Param("localDateTime")LocalDateTime localDateTime);
}
