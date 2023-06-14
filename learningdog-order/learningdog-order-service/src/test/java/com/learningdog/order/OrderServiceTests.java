package com.learningdog.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootApplication(scanBasePackages = {"com.learningdog.order"
        ,"com.learningdog.messagesdk",
        "com.learningdog.feign"})
class OrderServiceTests {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceTests.class,args);
    }

}
