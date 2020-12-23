package top.dongxibao.product;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.dongxibao.product.enums.QueueEnum;

/**
 * @ClassName OrderDlxExchangeTest
 * @Description 1
 * @Author Dongxibao
 * @Date 2020/12/23
 * @Version 1.0
 */
@SpringBootTest
public class OrderDlxExchangeTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 延时队列测试
     */
    @Test
    public void orderDlxExchangeTest() {
        rabbitTemplate.convertAndSend(QueueEnum.ORDER_RELEASE_QUEUE.getExchange(),QueueEnum.ORDER_DELAY_QUEUE.getRouteKey(),"订单创建...");
    }
}
