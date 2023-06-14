package com.learningdog.order.model.dto;

import com.learningdog.order.po.PayRecord;
import lombok.Data;

/**
 * @author: getjiajia
 * @description: 支付记录dto
 * @version: 1.0
 */
@Data
public class PayRecordDto extends PayRecord {
    //二维码
    private String qrcode;
}
