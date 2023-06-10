package com.learningdog.checkcode.service.impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.learningdog.base.utils.EncryptUtil;
import com.learningdog.checkcode.model.dto.CheckCodeParamsDto;
import com.learningdog.checkcode.model.dto.CheckCodeResultDto;
import com.learningdog.checkcode.service.AbstractCheckCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author: getjiajia
 * @description: 图片验证码生成器
 * @version: 1.0
 */
@Component("picCheckCodeService")
@Slf4j
public class PicCheckCodeServiceImpl extends AbstractCheckCodeService {
    @Resource
    DefaultKaptcha defaultKaptcha;

    @Override
    @Resource(name = "numberLetterCheckCodeGenerator")
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator=checkCodeGenerator;
    }

    @Override
    @Resource(name = "UUIDKeyGenerator")
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator=keyGenerator;
    }

    @Override
    @Resource(name = "redisCheckCodeStore")
    public void setCheckCodeStore(CheckCodeStore checkCodeStore) {
        this.checkCodeStore=checkCodeStore;
    }

    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {
        GenerateResult generateResult=generate(checkCodeParamsDto,4,"checkcode:",60*5);
        String key=generateResult.getKey();
        String code = generateResult.getCode();
        String pic=createPic(code);
        CheckCodeResultDto checkCodeResultDto=new CheckCodeResultDto();
        checkCodeResultDto.setKey(key);
        checkCodeResultDto.setAliasing(pic);
        return checkCodeResultDto;
    }




    private String createPic(String code) {
        // 生成图片验证码
        BufferedImage image = defaultKaptcha.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String imgBase64Encoder = null;
        try {
            // 对字节数组Base64编码
            ImageIO.write(image, "png", outputStream);
            imgBase64Encoder = "data:image/png;base64," + EncryptUtil.encodeBase64(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgBase64Encoder;
    }

    @Override
    public CheckCodeResultDto generateKey(String prefix) {
        //生成一个key
        String key = keyGenerator.generate(prefix+":");
        log.debug("生成key:{}",key);
        //存储验证码
        checkCodeStore.set(key,prefix,60*5);
        CheckCodeResultDto checkCodeResultDto=new CheckCodeResultDto();
        checkCodeResultDto.setKey(key);
        return checkCodeResultDto;
    }
}
