package top.dongxibao.product;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestRabbitmqProductApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    void helloWord() {
        /**
         * 发送消息
         * 参数一：路由key
         * 参数二：发送的消息
         */
        rabbitTemplate.convertAndSend("HelloWorld","HelloWorld消息...");
    }
    @Test
    void workQueue() {
        for (int i = 0; i < 50; i++) {
            /**
             * 发送消息
             * 参数一：路由key
             * 参数二：发送的消息
             */
            rabbitTemplate.convertAndSend("WorkQueue","workQueue消息 ---> " + i);
        }
    }
    @Test
    void fanoutQueue() {
        for (int i = 0; i < 10; i++) {
            /**
             * 发送消息
             * 参数一：交换机名称
             * 参数二：路由key：不管指定什么路由key都会转发到绑定交换机的所有队列
             * 参数三：发送的消息
             */
            rabbitTemplate.convertAndSend("fanoutExchange", "WorkQueue","fanoutQueue消息 ---> " + i);
        }
    }
    @Test
    void directQueue() {
        /**
         * 发送消息
         * 参数一：交换机名称
         * 参数二：路由key：
         * 参数三：发送的消息
         */
        rabbitTemplate.convertAndSend("directExchange", "insert.direct.queue","insert.direct.queue消息...");
        rabbitTemplate.convertAndSend("directExchange", "update.direct.queue","update.direct.queue消息...");
        rabbitTemplate.convertAndSend("directExchange", "delete.direct.queue","delete.direct.queue消息...");
    }
    @Test
    void topicsQueue() {
        /**
         * 发送消息
         * 参数一：交换机名称
         * 参数二：路由key：
         * 参数三：发送的消息
         */
        rabbitTemplate.convertAndSend("topicsExchange", "order.insert","order.insert消息...");
        rabbitTemplate.convertAndSend("topicsExchange", "order.update","order.update消息...");
        rabbitTemplate.convertAndSend("topicsExchange", "item.insert","item.insert消息...");
        rabbitTemplate.convertAndSend("topicsExchange", "item.update","item.update消息...");
    }

    @Test
    void ttlMessage() {
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置消息 5秒后过期
                message.getMessageProperties().setExpiration("5000");
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            }
        };
        rabbitTemplate.convertAndSend("ttlExchange","ttlMessageQueue","消息过期时间测试...", messagePostProcessor);
        rabbitTemplate.convertAndSend("ttlExchange","ttlQueue","队列过期时间测试...");
    }

    @Test
    void dlxQueue() {
        rabbitTemplate.convertAndSend("normalExchange","maxDlxQueue","死信队列测试-这是maxDlxQueue第一条消息...");
        rabbitTemplate.convertAndSend("normalExchange","maxDlxQueue","死信队列测试-这是maxDlxQueue第二条消息...");
        rabbitTemplate.convertAndSend("normalExchange","maxDlxQueue","死信队列测试-这是maxDlxQueue第三条消息...");
        rabbitTemplate.convertAndSend("normalExchange","ttlDlxQueue","死信队列测试-过期时间测试...");
    }

    @Test
    public void testFailQueueTest() {
        Message message = MessageBuilder.withBody("我们发送的消息内容存放在message的body里面".getBytes()).build();
        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        rabbitTemplate.convertAndSend("testExchange","","测试消息抵达Queue失败情况...");
    }
}
