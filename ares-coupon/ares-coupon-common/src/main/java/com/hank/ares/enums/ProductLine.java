package com.hank.ares.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 产品线枚举
 */
@Getter
@AllArgsConstructor
public enum ProductLine {

    DAMAO(1, "大猫"),
    DABAO(2, "大宝");

    /**
     * 产品线编码
     */
    private final Integer code;
    /**
     * 产品线描述
     */
    private final String description;

    public static ProductLine of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(i -> i.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists!"));
    }
}
