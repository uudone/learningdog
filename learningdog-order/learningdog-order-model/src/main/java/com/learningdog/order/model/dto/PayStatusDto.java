package com.learningdog.order.model.dto;

import lombok.Data;

/**
 * @author: getjiajia
 * @description: 支付结果dto, 用于接收支付结果通知处理逻辑
 * @version: 1.0
 */
@Data
public class PayStatusDto {
    //商户订单号
    private String out_trade_no;
    //支付宝交易号
    private String trade_no;
    //交易状态
    private String trade_status;
    //appid
    private String app_id;
    //total_amount
    private String total_amount;
}
