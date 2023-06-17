package com.learningdog.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.OrderStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.base.utils.IdWorkerUtils;
import com.learningdog.base.utils.QRCodeUtil;
import com.learningdog.messagesdk.po.MqMessage;
import com.learningdog.messagesdk.service.MqMessageService;
import com.learningdog.order.mapper.GoodsMapper;
import com.learningdog.order.mapper.OrderMapper;
import com.learningdog.order.mapper.PayRecordMapper;
import com.learningdog.order.model.dto.AddOrderDto;
import com.learningdog.order.model.dto.PayRecordDto;
import com.learningdog.order.po.Goods;
import com.learningdog.order.po.Order;
import com.learningdog.order.po.PayRecord;
import com.learningdog.order.service.OrderService;
import com.learningdog.order.service.PayRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    OrderMapper orderMapper;
    @Resource
    PayRecordService payRecordService;
    @Resource
    GoodsMapper goodsMapper;
    @Resource
    OrderService orderService;
    @Value("${pay.urlpattern}")
    String urlPattern;
    @Resource
    MqMessageService mqMessageService;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Value("${paynotify.exchange}")
    String paynotify_exchange;

    @Override
    @Transactional
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {
        //添加订单
        Order order = orderService.saveOrder(userId, addOrderDto);
        //添加支付交易记录
        PayRecord payRecord = payRecordService.savePayRecord(order);
        //生成二维码
        String qrCode = generateQRCode(urlPattern, payRecord.getPayNo());
        //封装返回数据
        PayRecordDto payRecordDto=new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;
    }

    @Override
    @Transactional
    public Order saveOrder(String userId, AddOrderDto addOrderDto) {
        //幂等性处理,outBusinessId为选课记录id
        Order order=getOrderByOutBusinessId(addOrderDto.getOutBusinessId());
        if (order!=null){
            return order;
        }
        order=new Order();
        //填入订单信息
        BeanUtils.copyProperties(addOrderDto,order);
        long orderId= IdWorkerUtils.getInstance().nextId();
        order.setId(orderId);
        order.setCreateDate(LocalDateTime.now());
        order.setStatus(OrderStatus.UNPAID);
        order.setUserId(userId);
        int insert = orderMapper.insert(order);
        if (insert<=0){
            log.debug("保存订单表失败，userId:{},addOrderDto:{}",userId,addOrderDto);
            LearningdogException.cast("提交订单失败，请重试");
        }
        List<Goods> goodsList= JSON.parseArray(addOrderDto.getOrderDetail(),Goods.class);
        goodsList.forEach(goods -> {
            Goods goodsNew=new Goods();
            BeanUtils.copyProperties(goods,goodsNew);
            goodsNew.setOrderId(orderId);
            goodsNew.setId(null);
            goodsMapper.insert(goodsNew);
        });
        return order;
    }

    @Override
    public void notifyPayResult(MqMessage mqMessage) {
        String msg=JSON.toJSONString(mqMessage);
        //消息持久化
        Message message= MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        //消息id
        CorrelationData correlationData=new CorrelationData(mqMessage.getId().toString());
        //添加ConfirmCallBack
        correlationData.getFuture().addCallback(
                result->{
                   if(result.isAck()) {
                       log.debug("通知支付结果消息发送成功, ID:{}", correlationData.getId());
                       //删除消息表中的记录
                       mqMessageService.completed(mqMessage.getId());
                   }else {
                       log.error("通知支付结果消息发送失败, ID:{}, 原因{}",correlationData.getId(), result.getReason());
                   }
                },
                ex->{
                    log.error("消息发送异常, ID:{}, 原因{}",correlationData.getId(),ex.getMessage());
                }
        );
        //发送消息
        rabbitTemplate.convertAndSend(paynotify_exchange,"",message,correlationData);
    }


    /**
     * @param outBusinessId:
     * @return Order
     * @author getjiajia
     * @description 通过outBusinessId查询Order
     */
    private Order getOrderByOutBusinessId(String outBusinessId) {
        return orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOutBusinessId,outBusinessId));
    }


    /**
     * @param urlPattern:
     * @param payNo:
     * @return String
     * @author getjiajia
     * @description 生成支付二维码
     */
    private String generateQRCode(String urlPattern,long payNo){
        String qrCode=null;
        try{
            String url=String.format(urlPattern,payNo);
            qrCode= QRCodeUtil.createQRCode(url,200,200);
        } catch (IOException e) {
            log.debug("生成二维码出错，payNo：{}",payNo);
            LearningdogException.cast("生成二维码出错");
        }
        return qrCode;
    }
}
