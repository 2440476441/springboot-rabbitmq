package com.dxc.example.springbootrabbitmq.work;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName WorkConsumer01
 * @CreateTime 2021-11-02 16:22
 * @Version 1.0
 * @Description: 消费者1
 */
@Component
public class WorkConsumer {
    @RabbitListener(queuesToDeclare = @Queue(value = "work"))
    public void receive(String message){
        System.out.println("消费者1："+message);
    }

    @RabbitListener(queuesToDeclare = @Queue(value = "work"))
    public void receive02(String message){
        System.out.println("消费者2："+message);
    }
}
