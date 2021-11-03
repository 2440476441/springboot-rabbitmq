package com.dxc.example.springbootrabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringBootRabbitmqApplicationTests {
    //注入模板对象
    @Autowired
    private RabbitTemplate rabbitTemplate;
    //直连模板
    @Test
    void contextLoads() {
        rabbitTemplate.convertAndSend("hello","秋风扫落叶🐏");
    }
    //Work模板
    @Test
    void contextLoads02() {
        for (int i=1;i<=10;i++){
            rabbitTemplate.convertAndSend("work",i+"_______只🐕");
        }
    }
    //Fanout模型
    @Test
    void test03(){
        rabbitTemplate.convertAndSend("logs","","fanout发送的消息");
    }
    //Routing模型
    @Test
    void test04(){
        rabbitTemplate.convertAndSend("rout","key01","routing发送的消息");
    }

}
