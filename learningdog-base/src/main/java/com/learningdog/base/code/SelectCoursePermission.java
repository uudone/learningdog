package com.learningdog.base.code;

/**
 * @author: getjiajia
 * @description: 选课学习资格
 * @version: 1.0
 */
public interface SelectCoursePermission {

    //正常学习
    String TO_LEARN="702001";
    //没有选课或选课后没有支付
    String UNPAID_OR_UNSELECT="702002";
    //已过期需要申请续期或重新支付
    String EXPIRED="702003";
}
