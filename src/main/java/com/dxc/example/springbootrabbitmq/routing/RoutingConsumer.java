package com.dxc.example.springbootrabbitmq.routing;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName FanoutConsumer
 * @CreateTime 2021-11-02 17:22
 * @Version 1.0
 * @Description: 消费者
 */
@Component
public class RoutingConsumer {
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,//绑定临时队列
                    exchange =@Exchange(value = "rout",type = "direct"), //绑定的交换机
                    key = {"key01"}
            )
    })
    public void receive01(String message){
        System.out.println("我是消费者01："+message);
    }
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,//绑定临时队列
                    exchange =@Exchange(value = "rout",type = "direct"), //绑定的交换机
                    key = {"key02"}
            )
    })
    public void receive02(String message){
        System.out.println("我是消费者02："+message);
    }
}
