package top.dongxibao.customer.controller;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**todo 不好用
 * 消费者监听类
 */
@Slf4j
@Component
public class WorkQueueRabbitListener2 {

    /******** 工作队列模式-消费者获取消息 ***************/
    @RabbitListener(queues = "WorkQueue")
    public void workQueue2(String msg, Channel channel) {
        /*try {
            channel.basicQos(1);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        log.info("工作队列workQueue2接收的消息：" + msg);
    }
}
