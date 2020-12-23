package top.dongxibao.customer.controller;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 消费者监听类
 */
@Slf4j
@Component
public class FanoutQueueRabbitListener {

    /******** Publish/Subscribe-消费者获取消息 ***************/
    @RabbitListener(queues = "fanoutQueue1")
    public void fanoutQueue1(String msg) {
        log.info("Publish/Subscribe 1模式接收的消息：" + msg);
    }

    @RabbitListener(queues = "fanoutQueue2", concurrency = "8")
    public void fanoutQueue2(String msg, Message message, Channel channel) {
        try {
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            if (deliveryTag % 2 == 0) {
                // long deliveryTag：唯一标识 ID, boolean multiple：是否批量
                channel.basicAck(deliveryTag, false);
                log.info("Publish/Subscribe 2模式签收了消息：" + msg);
            } else {
                // long deliveryTag：唯一标识 ID, boolean multiple：是否批量, boolean requeue：false丢弃；true发回服务器重新入队
                channel.basicNack(deliveryTag,false,false);
                // long deliveryTag, boolean requeue
//                channel.basicReject();
                log.info("Publish/Subscribe 2模式没有签收了消息：" + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
