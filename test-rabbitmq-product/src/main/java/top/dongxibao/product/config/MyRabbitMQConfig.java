package top.dongxibao.product.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.dongxibao.product.enums.QueueEnum;

/**
 * RabbitMQ配置类
 * 延时队列测试
 * @author Dongxibao
 * @date 2020-12-23
 */
@Slf4j
@Configuration
public class MyRabbitMQConfig {

    /******** Topics模式 ***************/
    /**
     * 声明交换机
     *
     * @return
     */
    @Bean("orderDlxExchange")
    public Exchange orderDlxExchange() {
        return ExchangeBuilder.topicExchange(QueueEnum.ORDER_DELAY_QUEUE.getExchange()).durable(true).build();
    }

    /**
     * 订单消息ttl队列
     *
     * @return
     */
    @Bean("orderDelayQueue")
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(QueueEnum.ORDER_DELAY_QUEUE.getName())
                .deadLetterExchange(QueueEnum.ORDER_DELAY_QUEUE.getExchange())
                .deadLetterRoutingKey(QueueEnum.ORDER_RELEASE_QUEUE.getRouteKey())
                .ttl(6000)
                .build();
    }

    /**
     * 指定的死信队列
     *
     * @return
     */
    @Bean("orderReleaseQueue")
    public Queue orderReleaseQueue() {
        return QueueBuilder.durable(QueueEnum.ORDER_RELEASE_QUEUE.getName()).build();
    }

    @Bean
    public Binding orderDelayQueueBinding(@Qualifier("orderDelayQueue") Queue queue,
                                          @Qualifier("orderDlxExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(QueueEnum.ORDER_DELAY_QUEUE.getRouteKey()).noargs();
    }

    @Bean
    public Binding orderReleaseQueueBinding(@Qualifier("orderReleaseQueue") Queue queue,
                                            @Qualifier("orderDlxExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(QueueEnum.ORDER_RELEASE_QUEUE.getRouteKey()).noargs();
    }
    /******** end ***************/
}
