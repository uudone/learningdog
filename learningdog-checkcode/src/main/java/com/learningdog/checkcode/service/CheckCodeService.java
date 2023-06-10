package com.learningdog.checkcode.service;

import com.learningdog.checkcode.model.dto.CheckCodeParamsDto;
import com.learningdog.checkcode.model.dto.CheckCodeResultDto;

/**
 * @author: getjiajia
 * @description: 验证码接口
 * @version: 1.0
 */
public interface CheckCodeService {
    /**
     * @param checkCodeParamsDto:
     * @return CheckCodeResultDto
     * @author getjiajia
     * @description 生成验证码
     */
    CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto);

    /**
     * @param key:
     * @param code:
     * @return boolean
     * @author getjiajia
     * @description 校验验证码
     */
    public boolean verify(String key, String code);

    /**
     * @param key:
     * @return Boolean
     * @author getjiajia
     * @description 校验key值
     */
    Boolean verifyKey(String key);

    /**
     * @param prefix:
     * @return CheckCodeResultDto
     * @author getjiajia
     * @description 生成校验key
     */
    CheckCodeResultDto generateKey(String prefix);

    /**
     * @author getjiajia
     * @description 验证码生成器
     */
    public interface CheckCodeGenerator{
        /**
         * 验证码生成
         * @return 验证码
         */
        String generate(int length);


    }

    /**
     * @author getjiajia
     * @description key生成器
     */
    public interface KeyGenerator{

        /**
         * key生成
         * @return 验证码
         */
        String generate(String prefix);
    }


    /**
     * @author getjiajia
     * @description 验证码存储
     */
    public interface CheckCodeStore{

        /**
         * @param key:
         * @param value:
         * @param expire: 过期时间，单位为秒
         * @return void
         * @author getjiajia
         * @description 向缓存设置key
         */
        void set(String key, String value, Integer expire);

        String get(String key);

        void remove(String key);
    }

}
