package top.dongxibao.customer;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableRabbit
@SpringBootApplication
public class TestRabbitmqCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestRabbitmqCustomerApplication.class, args);
    }

}
