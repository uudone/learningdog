package com.learningdog.base.code;

/**
 * @author: getjiajia
 * @description: 支付状态
 * @version: 1.0
 */
public interface PayStatus {
    //未支付
    String UNPAID="601001";
    //已支付
    String PAID="601002";
    //已退款
    String REFUNDED="601003";
}
