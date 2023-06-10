package com.learningdog.checkcode.service;

import com.learningdog.checkcode.model.dto.CheckCodeParamsDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @author: getjiajia
 * @description: 验证码接口
 * @version: 1.0
 */
@Slf4j
public abstract class AbstractCheckCodeService implements CheckCodeService{

    protected CheckCodeGenerator checkCodeGenerator;
    protected KeyGenerator keyGenerator;
    protected CheckCodeStore checkCodeStore;

    public abstract void  setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator);
    public abstract void  setKeyGenerator(KeyGenerator keyGenerator);
    public abstract void  setCheckCodeStore(CheckCodeStore checkCodeStore);


    /**
     * @param checkCodeParamsDto 生成验证码参数
     * @param code_length 验证码长度
     * @param keyPrefix key的前缀
     * @param expire 过期时间 单位为秒
     * @return GenerateResult
     * @author getjiajia
     * @description 生成验证公用方法
     */
    public GenerateResult generate(CheckCodeParamsDto checkCodeParamsDto, Integer code_length, String keyPrefix, Integer expire){
        //生成四位验证码
        String code = checkCodeGenerator.generate(code_length);
        log.debug("生成验证码:{}",code);
        //生成一个key
        String key = keyGenerator.generate(keyPrefix);
        log.debug("生成key:{}",key);
        //存储验证码
        checkCodeStore.set(key,code,expire);
        //返回验证码生成结果
        GenerateResult generateResult = new GenerateResult();
        generateResult.setKey(key);
        generateResult.setCode(code);
        return generateResult;
    }



    @Data
    protected class GenerateResult{
        String key;
        String code;
    }


    @Override
    public boolean verify(String key, String code){
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)){
            return false;
        }
        String code_l = checkCodeStore.get(key);
        if (code_l == null){
            return false;
        }
        boolean result = code_l.equalsIgnoreCase(code);
        if(result){
            //删除验证码
            checkCodeStore.remove(key);
        }
        return result;
    }

    @Override
    public Boolean verifyKey(String key) {
        if (StringUtils.isBlank(key)){
            return false;
        }
        String code_l = checkCodeStore.get(key);
        if (code_l == null){
            return false;
        }
        //删除验证码
        checkCodeStore.remove(key);
        return true;
    }

}