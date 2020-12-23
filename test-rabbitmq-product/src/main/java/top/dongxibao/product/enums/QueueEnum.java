package top.dongxibao.product.enums;

import lombok.Getter;

/**
 * @ClassName QueueEnum
 * @Description 队列
 * @Author Dongxibao
 * @Date 2020/12/23
 * @Version 1.0
 */
@Getter
public enum QueueEnum {
    /**
     * 订单创建死信队列
     */
    ORDER_DELAY_QUEUE("order-dlx-exchange", "order.delay.queue", "test.create.order"),
    /**
     * 订单创建过期后队列
     */
    ORDER_RELEASE_QUEUE("order-dlx-exchange", "order.release.queue", "test.release.order");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
