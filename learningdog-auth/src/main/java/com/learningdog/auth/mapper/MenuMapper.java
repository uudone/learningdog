package com.learningdog.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learningdog.auth.po.Menu;
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
public interface MenuMapper extends BaseMapper<Menu> {

    @Select("select * from menu where id in (\n" +
            "    select distinct menu_id from permission where role_id in (\n" +
            "        select role_id from user join user_role on user.id = user_role.user_id where user.id=#{userId}\n" +
            "        )\n" +
            "    )")
    List<Menu> selectPermissionByUserId(@Param("userId") String userId);

}
