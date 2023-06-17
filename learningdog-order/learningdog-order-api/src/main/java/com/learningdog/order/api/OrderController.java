package com.learningdog.order.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.learningdog.base.code.PayStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.order.config.AlipayConfig;
import com.learningdog.order.model.dto.AddOrderDto;
import com.learningdog.order.model.dto.PayRecordDto;
import com.learningdog.order.model.dto.PayStatusDto;
import com.learningdog.order.po.PayRecord;
import com.learningdog.order.service.OrderService;
import com.learningdog.order.service.PayRecordService;
import com.learningdog.order.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Controller
@Api(value = "订单支付接口", tags = "订单支付接口")
public class OrderController {

    @Value("${pay.alipay.APP_ID}")
    private String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    private String APP_PRIVATE_KEY;
    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    private String ALIPAY_PUBLIC_KEY;

    @Resource
    private OrderService  orderService;
    @Resource
    PayRecordService payRecordService;


    @ApiOperation("生成支付二维码")
    @ResponseBody
    @PostMapping("/generatepaycode")
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto){
        String userId= SecurityUtils.getUserId();
        return orderService.createOrder(userId,addOrderDto);
    }


    @ApiOperation("扫码支付接口")
    @GetMapping("/open/requestpay")
    public void requestPay(String payNo, HttpServletResponse response) throws IOException{
        if (payNo==null){
            LearningdogException.cast("请重新点击支付获取二维码");
        }
        Long payno=Long.parseLong(payNo);
        PayRecord payRecord = payRecordService.getPayRecordByPayNo(payno);
        if (payRecord==null){
            LearningdogException.cast("请重新点击支付获取二维码");
        }
        //支付状态
        String status=payRecord.getStatus();
        if (PayStatus.PAID.equals(status)){
            LearningdogException.cast("订单已支付，请勿重复支付。");
        }
        //构造sdk的客户端对象
        //获得初始化的AlipayClient
        AlipayClient alipayClient=new DefaultAlipayClient(AlipayConfig.URL,APP_ID,APP_PRIVATE_KEY,AlipayConfig.FORMAT
                ,AlipayConfig.CHARSET,ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);

        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        //alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\""+payRecord.getPayNo()+"\"," +
                "    \"total_amount\":"+payRecord.getTotalPrice()+"," +
                "    \"subject\":\""+payRecord.getOrderName()+"\"," +
                "    \"product_code\":\"QUICK_WAP_WAY\"" +
                "  }");//填充业务参数
        String form = null; //调用SDK生成表单
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            log.debug("调用SDK生成表单失败，payNo:{}",payNo);
            LearningdogException.cast("支付失败，请重试");
        }
        response.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        response.getWriter().write(form);//直接将完整的表单html输出到页面
        response.getWriter().flush();
        response.getWriter().close();
    }

    @ApiOperation("查询支付结果")
    @GetMapping("/payresult")
    @ResponseBody
    public PayRecordDto payResult(String payNo){
        String userId=SecurityUtils.getUserId();
        return payRecordService.queryPayResult(userId,payNo);
    }

    @ApiOperation("接收支付结果通知")
    @PostMapping("/receivenotify")
    public void receiveNotify(HttpServletRequest request,HttpServletResponse response) throws IOException {
        log.debug("异步接收支付结果通知");
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean verify_result = false;
        try {
            verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        if(verify_result) {//验证成功
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
            //appid
            String app_id=new String(request.getParameter("app_id").getBytes("ISO-8859-1"),"UTF-8");
            //total_amount
            String total_amount=new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");
            //交易成功
            if (trade_status.equals("TRADE_SUCCESS")) {
                PayStatusDto payStatusDto=new PayStatusDto();
                payStatusDto.setOut_trade_no(out_trade_no);
                payStatusDto.setTrade_no(trade_no);
                payStatusDto.setTrade_status(trade_status);
                payStatusDto.setApp_id(app_id);
                payStatusDto.setTotal_amount(total_amount);
                payRecordService.saveAliPayStatus(payStatusDto);
            }
            response.getWriter().write("success");
        }else{
            response.getWriter().write("fail");
        }

    }

}
