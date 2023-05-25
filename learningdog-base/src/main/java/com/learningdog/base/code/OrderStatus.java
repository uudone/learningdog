package com.learningdog.base.code;

/**
 * @author: getjiajia
 * @description: 订单交易状态
 * @version: 1.0
 */
public interface OrderStatus {
    //未支付
    String UNPAID="600001";
    //已支付
    String PAID="600002";
    //已关闭
    String CLOSED="600003";
    //已退款
    String REFUNDED="600004";
    //已完成
    String COMPLETED="600005";
}
