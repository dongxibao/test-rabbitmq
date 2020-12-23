package top.dongxibao.product.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Slf4j
@Configuration
public class RabbitMQConfig {
    private RabbitTemplate rabbitTemplate;
    /** 容器中的Queue、Exchange、Binding 会自动创建（在RabbitMQ）不存在的情况下
     *  如果RabbitMQ有，@Bean声明属性发生变化也不会覆盖
     */
    /**
     * json序列化
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        initRabbitTemplate();
        return rabbitTemplate;
    }

    /**
     * 定制RabbitTemplate
     * 1、服务收到消息就会回调
     *      1、spring.rabbitmq.publisher-confirms: true
     *      2、设置确认回调
     * 2、消息正确抵达队列就会进行回调
     *      1、spring.rabbitmq.publisher-returns: true
     *         spring.rabbitmq.template.mandatory: true
     *      2、设置确认回调ReturnCallback
     * 3、消费端确认(保证每个消息都被正确消费，此时才可以broker删除这个消息)
     */
    public void initRabbitTemplate() {

        /** 设置确认回调
         * 1、只要消息抵达Broker就ack=true
         * correlationData：当前消息的唯一关联数据(这个是消息的唯一id)
         * ack：消息是否成功收到
         * cause：失败的原因
         */
        rabbitTemplate.setConfirmCallback((correlationData,ack,cause) -> {
            log.info("confirm...correlationData[{}]==>ack:[{}]==>cause:[{}]", correlationData, ack, cause);
        });

        /**
         * 只要消息没有投递给指定的队列，就触发这个失败回调
         * message：投递失败的消息详细信息
         * replyCode：回复的状态码
         * replyText：回复的文本内容
         * exchange：当时这个消息发给哪个交换机
         * routingKey：当时这个消息用哪个路邮键
         */
        rabbitTemplate.setReturnCallback((message,replyCode,replyText,exchange,routingKey) -> {
            log.info("Fail Message[{}]==>replyCode[{}]==>replyText[{}]==>exchange[{}]==>routingKey[{}]",
                    message, replyCode, replyText, exchange, routingKey);
        });
    }


    /******** 简单模式-声明队列 ***************/
    @Bean("helloWorld")
    public Queue helloWorld() {
        return QueueBuilder.durable("HelloWorld").build();
    }
    /******** end ***************/

    /******** 工作队列模式-声明队列 ***************/
    @Bean("workQueue")
    public Queue workQueue() {
        return QueueBuilder.durable("WorkQueue").build();
    }
    /******** end ***************/

    /******** Publish/Subscribe模式 ***************/
    /**
     * 声明交换机
     *
     * @return
     */
    @Bean("fanoutExchange")
    public Exchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange("fanoutExchange").durable(true).build();
    }

    /**
     * 声明队列
     *
     * @return
     */
    @Bean("fanoutQueue1")
    public Queue fanoutQueue1() {
        return QueueBuilder.durable("fanoutQueue1").build();
    }

    @Bean("fanoutQueue2")
    public Queue fanoutQueue2() {
        return QueueBuilder.durable("fanoutQueue2").build();
    }

    /**
     * 绑定队列和交换机
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding fanoutQueue1Exchange(@Qualifier("fanoutQueue1") Queue queue,
                                        @Qualifier("fanoutExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("fanoutQueue1").noargs();
    }

    @Bean
    public Binding fanoutQueue2Exchange(@Qualifier("fanoutQueue2") Queue queue,
                                        @Qualifier("fanoutExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("fanoutQueue2").noargs();
    }
    /******** end ***************/

    /******** Routing模式 ***************/
    /**
     * 声明交换机
     *
     * @return
     */
    @Bean("directExchange")
    public Exchange directExchange() {
        return ExchangeBuilder.directExchange("directExchange").durable(true).build();
    }

    /**
     * 新增、修改队列
     *
     * @return
     */
    @Bean("directQueue1")
    public Queue directQueue1() {
        return QueueBuilder.durable("directQueue1").build();
    }

    /**
     * 新增、删除队列
     *
     * @return
     */
    @Bean("directQueue2")
    public Queue directQueue2() {
        return QueueBuilder.durable("directQueue2").build();
    }

    /**
     * directQueue1队列绑定新增和修改
     */
    @Bean
    public Binding insertDirectQueue1Exchange(@Qualifier("directQueue1") Queue queue,
                                              @Qualifier("directExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("insert.direct.queue").noargs();
    }

    @Bean
    public Binding updateDirectQueue1Exchange(@Qualifier("directQueue1") Queue queue,
                                              @Qualifier("directExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("update.direct.queue").noargs();
    }

    /**
     * directQueue2队列绑定新增和删除
     */
    @Bean
    public Binding insertDirectQueue2Exchange(@Qualifier("directQueue2") Queue queue,
                                              @Qualifier("directExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("insert.direct.queue").noargs();
    }

    @Bean
    public Binding deleteDirectQueue2Exchange(@Qualifier("directQueue2") Queue queue,
                                              @Qualifier("directExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("delete.direct.queue").noargs();
    }
    /******** end ***************/

    /******** Topics模式 ***************/
    /**
     * 声明交换机
     *
     * @return
     */
    @Bean("topicsExchange")
    public Exchange topicsExchange() {
        return ExchangeBuilder.topicExchange("topicsExchange").durable(true).build();
    }

    /**
     * 订单相关操作队列
     *
     * @return
     */
    @Bean("orderTopicsQueue")
    public Queue orderTopicsQueue() {
        return QueueBuilder.durable("orderTopicsQueue").build();
    }

    /**
     * 新增相关操作队列
     *
     * @return
     */
    @Bean("insertTopicsQueue")
    public Queue insertTopicsQueue() {
        return QueueBuilder.durable("insertTopicsQueue").build();
    }

    @Bean
    public Binding orderTopicsQueueExchange(@Qualifier("orderTopicsQueue") Queue queue,
                                            @Qualifier("topicsExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("order.#").noargs();
    }

    @Bean
    public Binding insertTopicsQueueExchange(@Qualifier("insertTopicsQueue") Queue queue,
                                             @Qualifier("topicsExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("#.insert").noargs();
    }
    /******** end ***************/

    /******** 过期时间测试 ***************/
    /**
     * 声明交换机
     *
     * @return
     */
    @Bean("ttlExchange")
    public Exchange ttlExchange() {
        return ExchangeBuilder.topicExchange("ttlExchange").durable(true).build();
    }

    /**
     * 消息过期时间队列
     *
     * @return
     */
    @Bean("ttlMessageQueue")
    public Queue ttlMessageQueue() {
        return QueueBuilder.durable("ttlMessageQueue").build();
    }

    /**
     * 过期时间队列
     *
     * @return
     */
    @Bean("ttlQueue")
    public Queue ttlQueue() {
        // 设置队列过期时间为6s
        return QueueBuilder.durable("ttlQueue").ttl(6000).build();
    }

    @Bean
    public Binding ttlMessageQueueExchange(@Qualifier("ttlMessageQueue") Queue queue,
                                           @Qualifier("ttlExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("ttlMessageQueue").noargs();
    }

    @Bean
    public Binding ttlQueueExchange(@Qualifier("ttlQueue") Queue queue,
                                    @Qualifier("ttlExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("ttlQueue").noargs();
    }
    /******** end ***************/

    /******** 死信队列测试 ***************/
    /**
     * 声明正常交换机
     * @return
     */
    @Bean("normalExchange")
    public Exchange normalExchange() {
        return ExchangeBuilder.topicExchange("normalExchange").durable(true).build();
    }
    /**
     * 声明死信交换机
     * @return
     */
    @Bean("dlxExchange")
    public Exchange dlxExchange() {
        return ExchangeBuilder.topicExchange("dlxExchange").durable(true).build();
    }

    /**
     * 死信交换机转发的队列
     *
     * @return
     */
    @Bean("myDlxQueue")
    public Queue myDlxQueue() {
        return QueueBuilder.durable("myDlxQueue").build();
    }

    /**
     * 最大长度队列
     *
     * @return
     */
    @Bean("maxDlxQueue")
    public Queue maxDlxQueue() {
        return QueueBuilder
                .durable("maxDlxQueue")
                .maxLength(2).deadLetterExchange("dlxExchange").deadLetterRoutingKey("myDlxQueue")
                // 超过长度后转发的死信交换机
//                .withArgument("x-dead-letter-exchange", "dlxExchange")
//                // 超过长度后转发的路由键
//                .withArgument("x-dead-letter-routing-key", "myDlxQueue")
                .build();
    }

    /**
     * 过期时间队列
     *
     * @return
     */
    @Bean("ttlDlxQueue")
    public Queue ttlDlxQueue() {
        // 设置队列过期时间为6s
        return QueueBuilder
                .durable("ttlDlxQueue").deadLetterExchange("dlxExchange").deadLetterRoutingKey("myDlxQueue")
                // 时间到期后转发的死信交换机
//                .withArgument("x-dead-letter-exchange", "dlxExchange")
//                // 时间到期后转发的路由键
//                .withArgument("x-dead-letter-routing-key", "myDlxQueue")
                .ttl(6000)
                .build();
    }

    @Bean
    public Binding maxDlxQueueExchange(@Qualifier("maxDlxQueue") Queue queue,
                                       @Qualifier("normalExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("maxDlxQueue")
                .noargs();
    }

    @Bean
    public Binding ttlDlxQueueExchange(@Qualifier("ttlDlxQueue") Queue queue,
                                       @Qualifier("normalExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("ttlDlxQueue").noargs();
    }
    /******** end ***************/
    @Bean("testExchange")
    public Exchange testExchange() {
        return ExchangeBuilder.topicExchange("testExchange").durable(true).build();
    }
}
