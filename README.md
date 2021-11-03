# MQ引言及不同MQ特点

## 引言 

MQ（message queue），翻译为**消息队列**，通过典型的生产者消费者模型向详细队列中生产消息，消费者 不断的从队列中获取消息。因为消息的生产和消费都是异步的，而且只关心消息的发送和接收，没有业务逻辑的侵入，轻松的实现系统间解耦。别名为**消息中间件**。通过高效可靠的消息传递机制进行平台无关的数据交流。并基于数据通信来进行分布式系统的集成。

## 不同MQ的特点

![image-20211026160827046](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211026160827046.png)

RabbitMQ的特点：基于AMQP协议，erlang语言开发，是部署最广泛的开源消息中间件，是最受欢迎的开源消息中间件之一

AMQP：一种高级消息传输协议

![image-20211028093630113](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211028093630113.png)

![image-20211028094451875](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211028094451875.png)

# 安装RabbitMQ

linux系统中安装RabbitMQ：

![image-20211028095755275](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211028095755275.png)

![image-20211028100301955](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211028100301955.png)

↑此操作的目的是开放游客用户的访问权限

![image-20211028100506005](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211028100506005.png)

![image-20211028100801578](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211028100801578.png)

![image-20211028100924811](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211028100924811.png)

![image-20211028101116834](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211028101116834.png)

![image-20211028101154254](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211028101154254.png)

# Docker安装RabbitMQ

https://www.cnblogs.com/yufeng218/p/9452621.html

# 一、直连模式

![image-20211029140529871](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211029140529871.png)

## 引入依赖

```xml
<!--        引入rabbitmq的相关依赖-->
        <dependency>
            <groupId>com.rabbitmq</groupId>
            <artifactId>amqp-client</artifactId>
            <version>5.12.0</version>
        </dependency>
```

## 获取连接

```java
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitUtil {
    private RabbitUtil(){};

    public static Connection getConnection() throws IOException, TimeoutException {
        //创建连接mq的连接工厂对象
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置连接rabbitmq主机
        connectionFactory.setHost("47.100.xx.xx");
        //设置端口号
        connectionFactory.setPort(5672);
        //设置连接哪个虚拟主机
        connectionFactory.setVirtualHost("/rabbit");
        //设置访问虚拟主机的用户名和密码
        connectionFactory.setUsername("rabbit");
        connectionFactory.setPassword("xxxxx");
        //利用工厂获取连接对象
        Connection connection = connectionFactory.newConnection();
        return connection;
    }
}

```

## 生产者代码

```java
import com.dxc.util.RabbitUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.Test;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    @Test
    //生产消息
    public void sendMessage() throws IOException, TimeoutException {
        Connection connection = RabbitUtil.getConnection();
        //获取连接通道
        Channel channel = connection.createChannel();
        //通道绑定对应消息队列
        /**
         * 参数1：队列名称 如队列不存在自动创建
         * 参数2：用来定义队列特性是否要持久化 true持久化队列   false不持久化
         * 参数3；是否独占队列 true独占队列   false不独占队列
         * 参数4：是否在消费完成后自动删除队列   ture自动删除  false不自动删除
         * */
        channel.queueDeclare("hello",false,false,false,null);
        //发布消息
        /**
         * 参数1：交换机名称
         * 参数2：队列名称
         * 参数3：传递消息额外设置
         * 参数4：消息的具体内容
         * */
        channel.basicPublish("","hello",null,"天王盖地虎".getBytes());
        //关闭通道
        channel.close();
        //关闭连接
        connection.close();
    }
}

```

## 消费者代码

```java
import com.dxc.util.RabbitUtil;
import com.rabbitmq.client.*;
import org.junit.Test;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接对象
        Connection connection = RabbitUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //绑定通道对象
        channel.queueDeclare("hello",false,false,false,null);
        //消费消息
        //参数1：消费那个队列的消息 队列名称
        //参数2：开始消息的自动确认机制
        //参数3：消费时的回调接口
        channel.basicConsume("hello",true,new DefaultConsumer(channel){
            @Override
            //最后一个参数：消息队列中取出来的消息
            public void handleDelivery(String consumerTag, Envelope envelop, AMQP.BasicProperties properties,byte[] body) throws IOException{
                System.out.println(new String(body));
            }
        });
//        消费者模型中一般不主动关闭
//        //关闭通道
//        channel.close();
//        //关闭连接
//        connection.close();
    }
}

```

## PS

注意，在消费者模型中，使用的是main函数。消费者模型需要以监听的形式获取消息，大多数情况下我们从队列中获取消息后，需要做一些处理，此时不能关闭通道和连接。所以，如果使用junit测试方法，在测试方法之后结束后会强制关闭连接，这就会导致我们只从队列中消费了消息，却没有执行回调函数，没有执行一些诸如打印之类的操作。

所以，一般在消费者模型中我们不会主动关闭通道和连接。

# 二、Work queue（任务模型）

Work queues，也被称为（Task queues），任务模型。当消息处理比较耗时的时候，可能生产消息的速度会远远大于消息的消费速度。长此以往，消息就会堆积越来越多，无法及时处理。此时就可以使用work 模型：
让多个消费者绑定到一个队列，共同消费队列中的消息。
队列中的消息一旦消费，就会消失，因此任务是不会被重复执行的。

![image-20211029140503434](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211029140503434.png)

在这种情况下，消费者消费信息默认采取平均分配的机制，那么这样就会导致一个问题，即消费能力较低的消费者会拖慢整体消费速度，例如队列中有100条消息待消费，消费速度较快的C1消费完成后，需要等待消费速度较慢的C2消费完成。而在这段时间，C1却是空闲状态。消费100条消息的整体速度被拉慢

![image-20211029144725300](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211029144725300.png)

![image-20211029144739299](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211029144739299.png)

## 消息确认机制

rabbitmq之所以采用默认的平均分配，是由其消息确认机制确定的

自动确认机制：

![image-20211101111657237](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211101111657237.png)

在这段代码中，当开启自动确认机制时，无论消费者是否执行完@Override的业务代码，队列都会自动确认消息，并从队列中删除这条消息，就是只管发，不管处理不处理的完。这样会造成一个问题：假如消费者在执行业务代码时突然中断并宕机，会造成消息丢失。所以一般不采用自动确认机制。

## 能者多劳的模式

![image-20211101112116721](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211101112116721.png)

在这段改进的代码中加入了

```java
channel.basicQos(1);
```

的目的是使得此消费者一次性只处理一条消息。

将消费者的自动确认机制关闭（参数2置为false）后，需要我们在执行业务代码后手动确认消息。

这样做有两个好处：

- 1.当某一消费者迟迟没有确认消息时，队列并不会删除这条消息，而是会在队列中保留。
- 2.在某一消费者暂时没有确认时，其他消费者依然可以从队列中消费消息，这样就做到了能者多劳的模式。处理的快的消费者可以拿到更多的消息，处理慢的拿到的消息较少。

# 三、Fanout模型（扇出、广播）

![image-20211101114526410](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211101114526410.png)

广播模式下、消息发送流程是这样的：

![image-20211101114600538](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211101114600538.png)

## 生产者代码

```java
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtil.getConnection();
        Channel channel = connection.createChannel();
        //将通道绑定交换机
        //参数1：交换机名称
        //参数2：交换机类型   fanout 广播类型
        channel.exchangeDeclare("regist","fanout");
        //发送消息
        channel.basicPublish("regist","",null,"一条广播消息".getBytes());
        channel.close();
        connection.close();
    }
}
```

## 消费者代码

```java
public class Consumer01 {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtil.getConnection();
        Channel channel = connection.createChannel();
        //通道绑定交换机
        channel.exchangeDeclare("regist","fanout");
        //临时队列
        String queue = channel.queueDeclare().getQueue();
        //绑定交换机和队列
        channel.queueBind(queue,"regist","");
        //消费消息
        channel.basicConsume(queue,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者1"+new String(body));
            }
        });
    }
}
```

消费者02、消费者03同消费者01，当生产者将产出的消息发送到交换机后，消费者01、02、03会同时收到消息

PS：每个临时队列都会在消费者停止监听时销毁

# 四、Direct模型（路由模型）

![image-20211101141355710](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211101141355710.png)

![image-20211101141414547](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211101141414547.png)

![image-20211101141343678](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211101141343678.png)

## 生产者代码

```java
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtil.getConnection();
        Channel channel = connection.createChannel();
        //绑定交换机
        //参数1：交换机名称
        //参数2：交换机类型   direct：路由模式
        channel.exchangeDeclare("logs","direct");
        //发送消息
        String routingKey = "info";
        channel.basicPublish("logs",routingKey,null,"一拳超人".getBytes());
        channel.close();
        connection.close();
    }
}
```

## 消费者01代码

（监听routing key为error的消息）

```java
public class Consumer01 {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtil.getConnection();
        Channel channel = connection.createChannel();
        //通道声明交换机以及交换机类型
        channel.exchangeDeclare("logs","direct");
        //创建一个临时队列
        String queue = channel.queueDeclare().getQueue();
        //基于routingKey绑定队列和交换机
        channel.queueBind(queue,"logs","error");
        //获取消费的消息
        channel.basicConsume(queue,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new java.lang.String(body));
            }
        });
    }
}
```

## 消费者02代码

监听routing key为error、info、warning的消息

```java
public class Consumer02 {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtil.getConnection();
        Channel channel = connection.createChannel();
        //通道声明交换机以及交换机类型
        channel.exchangeDeclare("logs","direct");
        //创建一个临时队列
        String queue = channel.queueDeclare().getQueue();
        //基于routingKey绑定队列和交换机
        channel.queueBind(queue,"logs","error");
        channel.queueBind(queue,"logs","info");
        channel.queueBind(queue,"logs","warning");
        //获取消费的消息
        channel.basicConsume(queue,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
            }
        });
    }
}
```

不多说了，懂得都懂

# 五、Topic(动态路由模型)

![image-20211101151711588](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211101151711588.png)

![image-20211102141148600](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211102141148600.png)

![image-20211102141452440](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211102141452440.png)

项目代码略

# 六、SpringBoot整合RabbitMQ

## 1.搭建初始环境

引入依赖

```xml
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
            <version>2.5.2</version>
        </dependency>
```

配置文件

```yml
spring:
  application:
    name: application_rabbitmq
  rabbitmq:
    host: 47.100.42.118
    port: 5672
    username: rabbit
    password: 123456
    virtual-host: /rabbit
```

Spring为了简化rabbitmq的使用提供了一个模板对象：RabbitTemplate

## 2.直连模型

生产者

```java
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
}
```

消费者

```java
@Component
//RabbitListener可选参数
//参数1：队列名称
//参数2：durable持久化，默认为false
//参数3：autoDelete自动删除，默认为false
@RabbitListener(queuesToDeclare = @Queue(value = "hello"))
public class HelloConsumer {
    @RabbitHandler
    public void receive(String message){
        System.out.println("消费消息:"+message);
    }
}
```

PS：先执行生产者方法，可以看到在rabbitmq中并没有产生新的队列，这是因为此队列没有监听者在监听。所以代码顺序应该是先执行消费者监听，再执行生产者产生消息

## 3.Work模型

多个消费者的情况

```java
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
```

## 4.Fanout模型

生产者

```java
@Test
    void test03(){
        rabbitTemplate.convertAndSend("logs","","fanout发送的消息");
    }
```

消费者

```java
@Component
public class FanoutConsumer {
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,//绑定临时队列
                    exchange =@Exchange(value = "logs",type = "fanout") //绑定的交换机
            )
    })
    public void receive01(String message){
        System.out.println("我是消费者01："+message);
    }
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,//绑定临时队列
                    exchange =@Exchange(value = "logs",type = "fanout") //绑定的交换机
            )
    })
    public void receive02(String message){
        System.out.println("我是消费者02："+message);
    }
}
```

## 5.Routing模型

生产者

```java
@Test
void test04(){
    rabbitTemplate.convertAndSend("rout","key01","routing发送的消息");
}
```

消费者

```java
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
```

	## 6.Topic模型

基本和redirect差不多，只加了动态路由

# 七、MQ应用场景

## 1.异步处理

场景说明：用户注册后，需要发注册邮件和注册短信，传统的做法有两种1.串行的方式和并行的方式

![image-20211103115822112](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211103115822112.png)

![image-20211103131846730](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211103131846730.png)

## 2.应用解耦

![image-20211103132047892](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211103132047892.png)

## 3.流量削峰

![image-20211103132501530](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211103132501530.png)

# 八、RabbitMQ集群

## 1.普通集群(副本集群，了解即可)

![image-20211103132854810](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211103132854810.png)

![image-20211103132713916](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211103132713916.png)

核心解决问题：当集群中某一时刻master节点宕机，可以对Queue中信息进行备份

副本只拷贝交换机，不拷贝队列。  

## 2.镜像集群(重点)

 ![image-20211103134430058](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211103134430058.png)

![image-20211103135616671](https://quanjichao.oss-cn-beijing.aliyuncs.com/images/image-20211103135616671.png)



