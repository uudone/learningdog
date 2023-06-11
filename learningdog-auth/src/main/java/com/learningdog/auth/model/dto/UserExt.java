package com.learningdog.auth.model.dto;

import com.learningdog.auth.po.User;
import lombok.Data;

import java.util.List;

/**
 * @author: getjiajia
 * @description: uer扩展类
 * @version: 1.0
 */
@Data
public class UserExt extends User {
    private List<String> permissions;
}
