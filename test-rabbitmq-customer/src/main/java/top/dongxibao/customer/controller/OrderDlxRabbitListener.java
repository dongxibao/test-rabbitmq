package top.dongxibao.customer.controller;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 延时队列测试-消费者监听类
 * @author Dongxibao
 * @date 2020-12-23
 */
@Slf4j
@Component
public class OrderDlxRabbitListener {

    /******** 延时队列测试-消费者获取消息 ***************/
    @RabbitListener(queues = {"order.release.queue"})
    public void orderReleaseQueue(String msg, Channel channel, Message message) throws IOException {
        log.info("延时队列测试 消费者接收的消息：" + msg);
        try {
//            int i = 1/0;
            // long deliveryTag：唯一标识 ID, boolean multiple：是否批量
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            // 将消息重新放回队列，让别人消费。可能死循环，需要手动处理
            // long deliveryTag：唯一标识 ID, boolean requeue：false丢弃；true发回服务器重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            log.error("OrderDlxRabbitListener：[{}];[{}]",e,e.getMessage());
        }
    }
}
