package top.dongxibao.customer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费者监听类
 */
@Slf4j
@Component
public class TopicsQueueRabbitListener {

    /******** Topics-消费者获取消息 ***************/
    @RabbitListener(queues = "orderTopicsQueue")
    public void orderTopicsQueue(String msg) {
        log.info("Topics模式-orderTopicsQueue队列 接收的消息：" + msg);
    }
    @RabbitListener(queues = "insertTopicsQueue")
    public void insertTopicsQueue(String msg) {
        log.info("Topics模式-insertTopicsQueue队列 接收的消息：" + msg);
    }
}
