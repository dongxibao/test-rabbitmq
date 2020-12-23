package top.dongxibao.customer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费者监听类
 */
@Slf4j
@Component
public class HelloWorldRabbitListener {

    /******** 简单模式-消费者获取消息 ***************/
    @RabbitListener(queues = "HelloWorld")
    public void helloWorld(String msg) {
        System.out.println("简单模式接收的消息：" + msg);
    }
}
