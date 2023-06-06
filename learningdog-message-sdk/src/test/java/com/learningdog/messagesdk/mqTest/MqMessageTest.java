package com.learningdog.messagesdk.mqTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author: getjiajia
 * @description: TODO
 * @version: 1.0
 */
@SpringBootTest
public class MqMessageTest {
    @Resource
    MessageProcess messageProcess;

    @Test
    public void test() throws InterruptedException {
        System.out.println("开始执行-----》" + LocalDateTime.now());
        messageProcess.process(0, 1, "test", 5, 30);
        System.out.println("结束执行-----》" + LocalDateTime.now());
        Thread.sleep(9000000);

    }

}
