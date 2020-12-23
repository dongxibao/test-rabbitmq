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
public class WorkQueueRabbitListener {

    /******** 工作队列模式-消费者获取消息 ***************/
    @RabbitListener(queues = "WorkQueue")
    public void workQueue1(String msg, Channel channel) {
        /*try {
            Thread.sleep(1000);
            channel.basicQos(1);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        log.info("工作队列workQueue1接收的消息：" + msg);
    }
}
