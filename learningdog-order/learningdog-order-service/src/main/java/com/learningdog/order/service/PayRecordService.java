package com.learningdog.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.order.model.dto.PayRecordDto;
import com.learningdog.order.model.dto.PayStatusDto;
import com.learningdog.order.po.Order;
import com.learningdog.order.po.PayRecord;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-06-14
 */
public interface PayRecordService extends IService<PayRecord> {

    /**
     * @param order:
     * @return PayRecord
     * @author getjiajia
     * @description 保存支付记录
     */
    PayRecord savePayRecord(Order order);

    /**
     * @param payNo:
     * @return PayRecord
     * @author getjiajia
     * @description 查询支付交易记录
     */
    PayRecord getPayRecordByPayNo(Long payNo);

    /**
     * @param userId:
     * @param payNo:
     * @return PayRecordDto
     * @author getjiajia
     * @description 查询支付结果
     */
    PayRecordDto queryPayResult(String userId, String payNo);

    /**
     * @param payNo:
     * @return PayStatusDto
     * @author getjiajia
     * @description 从支付宝查询支付结果
     */
    public PayStatusDto queryPayResultFromAlipay(String payNo);

    /**
     * @param payStatusDto:
     * @return void
     * @author getjiajia
     * @description 保存支付宝支付结果
     */
    public void saveAliPayStatus(PayStatusDto payStatusDto) ;

}
