package top.dongxibao.customer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费者监听类
 */
@Slf4j
@Component
public class DirectQueueRabbitListener {

        /******** Routing-消费者获取消息 ***************/
        @RabbitListener(queues = "directQueue1")
        public void directQueue1(String msg) {
            log.info("Routing模式-directQueue1队列 接收的消息：" + msg);
        }
        @RabbitListener(queues = "directQueue2")
        public void directQueue2(String msg) {
            log.info("Routing模式-directQueue2队列 接收的消息：" + msg);
        }
}
