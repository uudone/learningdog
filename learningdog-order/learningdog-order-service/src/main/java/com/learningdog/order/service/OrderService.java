package com.learningdog.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.messagesdk.po.MqMessage;
import com.learningdog.order.model.dto.AddOrderDto;
import com.learningdog.order.model.dto.PayRecordDto;
import com.learningdog.order.po.Order;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-06-14
 */
public interface OrderService extends IService<Order> {

    /**
     * @param userId:
     * @param addOrderDto:
     * @return PayRecordDto: 支付交易记录(包括二维码)
     * @author getjiajia
     * @description 创建商品订单
     */
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * @param userId:
     * @param addOrderDto:
     * @return Order
     * @author getjiajia
     * @description 保存订单数据
     */
    Order saveOrder(String userId,AddOrderDto addOrderDto);

    /**
     * @param mqMessage:
     * @return void
     * @author getjiajia
     * @description 发送订单支付完成通知
     */
    void notifyPayResult(MqMessage mqMessage);


}
