package com.dxc.example.springbootrabbitmq.hello;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName HelloConsumer
 * @CreateTime 2021-11-02 15:36
 * @Version 1.0
 * @Description: 消费者
 */
@Component
//RabbitListener可选参数
//参数1：队列名称
//参数2：durable持久化，默认为false
//参数3：autoDelete自动删除，默认为false
@RabbitListener(queuesToDeclare = @Queue(value = "hello"))
public class HelloConsumer {
    @RabbitHandler
    public void receive(String message){
        System.out.println("滚啊"+message);
    }
}
