package com.learningdog.checkcode.service.impl;

import com.learningdog.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author: getjiajia
 * @description: 数字字母生成器
 * @version: 1.0
 */
@Component("numberLetterCheckCodeGenerator")
public class NumberLetterCheckCodeGenerator implements CheckCodeService.CheckCodeGenerator {

    @Override
    public String generate(int length) {
        String str="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int len=str.length();
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(len);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
