package com.hank.ares.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RabbitExchangeEnum {

    TEST("ares.exchange.test");

    /**
     * 交换机名称
     */
    private final String exchangeName;
}
