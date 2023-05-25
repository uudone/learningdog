package com.learningdog.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.system.model.po.Dictionary;

import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-05-25
 */
public interface DictionaryService extends IService<Dictionary> {
    /**
     * @param :
     * @return List<Dictionary>
     * @author getjiajia
     * @description 查询所有数据字典内容
     */
    List<Dictionary> queryAll();

    Dictionary getByCode(String code);

}
