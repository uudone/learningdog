package com.learningdog.messagesdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learningdog.messagesdk.po.MqMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author getjiajia
 */
public interface MqMessageMapper extends BaseMapper<MqMessage> {


    @Select("select * from mq_message where id%#{shardTotal}=#{shardIndex} " +
            "and message_type=#{type} and state='0' limit #{count}")
    List<MqMessage> getByShardIndex(@Param("shardIndex")int shardIndex,
                                    @Param("shardTotal")int shardTotal,
                                    @Param("type")String type,
                                    @Param("count")int count);

}
