package com.hank.ares.config.mq;

import com.hank.ares.enums.RabbitExchangeEnum;
import com.hank.ares.enums.RabbitQueueEnum;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private static final String userName = "admin";
    private static final String password = "admin";

    /**
     * 连接配置
     *
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    /**
     * 默认交换机
     *
     * @return
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(RabbitExchangeEnum.TEST.getExchangeName(), true, false);
    }

    /**
     * 队列一：测试队列
     *
     * @return
     */
    @Bean
    public Queue queue1() {
        return new Queue(RabbitQueueEnum.TEST.getQueueName(), true);
    }

    /**
     * 队列绑定一：测试队列绑定到默认交换机上，并指定路由key
     *
     * @return
     */
    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(queue1()).to(defaultExchange()).with(RabbitQueueEnum.TEST.getQueueKey());
    }
}
