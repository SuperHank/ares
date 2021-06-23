package com.hank.ares.enums.mq;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RabbitQueueEnum {

    TEST("ares.queue.test.key", "ares.queue.test");

    /**
     * 队列ID
     */
    private final String queueKey;
    /**
     * 队列名
     */
    private final String queueName;
}
