package com.learningdog.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.OrderStatus;
import com.learningdog.base.code.PayStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.base.utils.IdWorkerUtils;
import com.learningdog.order.config.AlipayConfig;
import com.learningdog.order.mapper.OrderMapper;
import com.learningdog.order.mapper.PayRecordMapper;
import com.learningdog.order.model.dto.PayRecordDto;
import com.learningdog.order.model.dto.PayStatusDto;
import com.learningdog.order.po.Order;
import com.learningdog.order.po.PayRecord;
import com.learningdog.order.service.PayRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class PayRecordServiceImpl extends ServiceImpl<PayRecordMapper, PayRecord> implements PayRecordService {

    @Resource
    PayRecordMapper payRecordMapper;
    @Resource
    PayRecordService payRecordService;
    @Resource
    OrderMapper orderMapper;
    @Value("${pay.alipay.APP_ID}")
    private String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    private String APP_PRIVATE_KEY;
    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    private String ALIPAY_PUBLIC_KEY;

    @Override
    @Transactional
    public PayRecord savePayRecord(Order order) {
        if(order==null){
            LearningdogException.cast("订单不存在");
        }
        if (OrderStatus.PAID.equals(order.getStatus())){
            LearningdogException.cast("订单已支付");
        }
        PayRecord payRecord=new PayRecord();
        //填入数据
        long payNo= IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);
        payRecord.setOrderId(order.getId());
        payRecord.setOrderName(order.getOrderName());
        payRecord.setTotalPrice(order.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus(PayStatus.UNPAID);
        payRecord.setUserId(order.getUserId());
        int insert = payRecordMapper.insert(payRecord);
        if (insert<=0){
            log.debug("保存支付记录失败，order:{}",order);
            LearningdogException.cast("保存支付记录失败，请重试");
        }
        return payRecord;
    }

    @Override
    public PayRecord getPayRecordByPayNo(Long payNo) {
        return payRecordMapper.selectOne(new LambdaQueryWrapper<PayRecord>()
                .eq(PayRecord::getPayNo,payNo));
    }

    @Override
    public PayRecordDto queryPayResult(String userId, String payNo) {
        PayRecord payRecord = payRecordMapper.selectOne(new LambdaQueryWrapper<PayRecord>()
                .eq(PayRecord::getPayNo, payNo));
        if (payRecord==null){
            LearningdogException.cast("请重新点击支付获取二维码");
        }
        if (!userId.equals(payRecord.getUserId())){
            LearningdogException.cast("没有权限查询他人支付结果");
        }
        if (PayStatus.PAID.equals(payRecord.getStatus())){
            PayRecordDto payRecordDto=new PayRecordDto();
            BeanUtils.copyProperties(payRecord,payRecordDto);
            return payRecordDto;
        }
        //从支付宝查询支付结果
        PayStatusDto payStatusDto=queryPayResultFromAlipay(payNo);
        //保存支付结果
        payRecordService.saveAliPayStatus(payStatusDto);
        //重新查询支付记录
        payRecord=getPayRecordByPayNo(Long.parseLong(payNo));
        PayRecordDto payRecordDto=new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        return payRecordDto;
    }

    @Override
    public PayStatusDto queryPayResultFromAlipay(String payNo) {
        //请求支付宝查询支付结果
        AlipayClient alipayClient=new DefaultAlipayClient(AlipayConfig.URL,APP_ID,APP_PRIVATE_KEY,AlipayConfig.FORMAT
                ,AlipayConfig.CHARSET,ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                LearningdogException.cast("请求支付查询查询失败");
            }
        } catch (AlipayApiException e) {
            log.error("请求支付宝查询支付结果异常:{}", e.toString(), e);
            LearningdogException.cast("请求支付查询查询失败");
        }
        //获取支付结果
        Map resultMap= JSON.parseObject(response.getBody(),Map.class);
        Map alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
        String trade_status = (String) alipay_trade_query_response.get("trade_status");
        String total_amount = (String) alipay_trade_query_response.get("total_amount");
        String trade_no = (String) alipay_trade_query_response.get("trade_no");
        //封装支付结果
        PayStatusDto payStatusDto=new PayStatusDto();
        payStatusDto.setTrade_status(trade_status);
        payStatusDto.setTotal_amount(total_amount);
        payStatusDto.setTrade_no(trade_no);
        payStatusDto.setApp_id(APP_ID);
        payStatusDto.setOut_trade_no(payNo);
        return payStatusDto;
    }


    @Override
    @Transactional
    public void saveAliPayStatus(PayStatusDto payStatusDto) {
        //支付记录号
        String payNo = payStatusDto.getOut_trade_no();
        PayRecord payRecord = getPayRecordByPayNo(Long.parseLong(payNo));
        if (payRecord==null){
            LearningdogException.cast("找不到支付记录");
        }
        //保存支付结果
        String trade_status = payStatusDto.getTrade_status();
        log.debug("收到支付结果:{},支付记录:{}}", payStatusDto,payRecord);
        if (!"TRADE_SUCCESS".equals(trade_status)){
            LearningdogException.cast("支付失败");
        }
        Float totalPrice = payRecord.getTotalPrice() * 100;
        Float totalAmount=Float.parseFloat(payStatusDto.getTotal_amount())*100;
        //校验
        if (!payStatusDto.getApp_id().equals(APP_ID)||totalPrice.intValue()!=totalAmount.intValue()){
            log.info("校验支付结果失败,支付记录:{},APP_ID:{},totalPrice:{}" ,payRecord,payStatusDto.getApp_id(),payStatusDto.getTotal_amount());
            LearningdogException.cast("校验支付结果失败");
        }
        //更新支付记录
        PayRecord payRecordNew=new PayRecord();
        payRecordNew.setStatus(PayStatus.PAID);
        payRecordNew.setOutPayChannel("Alipay");
        payRecordNew.setOutPayNo(payStatusDto.getTrade_no());//支付宝交易号
        payRecordNew.setPaySuccessTime(LocalDateTime.now());
        int update = payRecordMapper.update(payRecordNew, new LambdaUpdateWrapper<PayRecord>()
                .eq(PayRecord::getPayNo, payNo));
        if (update<=0){
            log.debug("更新支付记录状态失败:{}", payRecordNew);
            LearningdogException.cast("更新支付记录状态失败");
        }else{
            log.debug("更新支付记录状态成功:{}", payRecordNew);
        }
        //更新订单
        Long orderId = payRecord.getOrderId();
        Order order = orderMapper.selectById(orderId);
        if (order==null){
            log.debug("根据支付记录[{}}]找不到订单",payRecord);
            LearningdogException.cast("根据支付记录找不到订单");
        }
        int update1 = orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                .eq(Order::getId, orderId)
                .set(Order::getStatus, OrderStatus.PAID));
        if (update1<=0){
            log.info("更新订单表状态失败,订单号:{}", orderId);
            LearningdogException.cast("更新订单表状态失败");
        }else {
            log.info("更新订单表状态成功,订单号:{}", orderId);
        }
    }
}
