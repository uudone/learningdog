package com.learningdog.order.model.dto;

import com.learningdog.order.po.PayRecord;
import lombok.Data;
import lombok.ToString;

/**
 * @author: getjiajia
 * @description: 支付记录dto
 * @version: 1.0
 */
@Data
@ToString
public class PayRecordDto extends PayRecord {
    //二维码
    private String qrcode;
}
