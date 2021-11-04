package com.hank.ares.config.mq;

import com.hank.ares.enums.mq.RabbitExchangeEnum;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hank.ares.enums.mq.RabbitQueueEnum.TEST;

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
        connectionFactory.setHost("139.196.201.176");
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

    @Bean
    public Queue queue1() {
        return new Queue(TEST.getQueueName(), true);
    }

    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(queue1()).to(defaultExchange()).with(TEST.getQueueKey());
    }
}
