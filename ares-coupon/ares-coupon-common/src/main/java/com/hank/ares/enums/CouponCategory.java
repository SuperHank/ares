package com.hank.ares.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠券分类
 */
@Getter
@AllArgsConstructor
public enum CouponCategory {

    MANJIAN("001", "满减券"),
    ZHEKOU("002", "折扣券"),
    LIJIAN("003", "立减券");

    /**
     * 优惠券分类编码
     */
    private final String code;
    /**
     * 优惠券描述
     */
    private final String description;

    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(i -> i.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists!"));
    }
}
