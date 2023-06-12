package com.learningdog.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.learningdog.learning"
        ,"com.learningdog.messagesdk",
        "com.learningdog.feign"})
class ServiceTestStarter {

    public static void main(String[] args) {
        SpringApplication.run(ServiceTestStarter.class,args);
    }

}
