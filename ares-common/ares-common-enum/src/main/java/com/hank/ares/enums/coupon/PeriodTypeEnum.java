package com.hank.ares.enums.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 有效期类型
 */
@Getter
@AllArgsConstructor
public enum PeriodTypeEnum {

    REGULAR(1, "固定的（固定日期）"),
    SHIFT(2, "变动的（以领取之日开始计算）");

    /**
     * 有效期编码
     */
    private final Integer code;
    /**
     * 有效期描述
     */
    private final String description;

    public static PeriodTypeEnum of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(i -> i.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists!"));
    }
}
