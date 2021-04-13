package com.hank.ares;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * 测试消息处理器
 */
@Component
public class TestListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        byte[] body = message.getBody();
        System.out.println("收到消息 : " + new String(body));
        // 确认消息成功消费
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
