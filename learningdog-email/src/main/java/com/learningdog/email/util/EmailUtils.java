package com.learningdog.email.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author: getjiajia
 * @description: 邮件工具类
 * @version: 1.0
 */
public class EmailUtils {

    public static boolean setEmail(String from,String to,String header,String content,String secret){

        // 指定发送邮件的主机为 smtp.qq.com
        String host = "smtp.qq.com";  //QQ 邮件服务器

        // 获取系统属性
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(from, secret); //发件人邮件用户名、授权码
            }
        });

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject(header);

            // 发送 HTML 消息, 可以插入html标签
            message.setContent(content,
                    "text/html;charset=utf-8");

            // 发送消息
            Transport.send(message);
        }catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
        return true;
    }
}
