package com.hank.ares.enums.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠券分类
 */
@Getter
@AllArgsConstructor
public enum CouponCategoryEnum {

    MANJIAN(1, "满减券"),
    ZHEKOU(2, "折扣券"),
    LIJIAN(3, "立减券");

    /**
     * 优惠券分类编码
     */
    private final Integer code;
    /**
     * 优惠券描述
     */
    private final String description;

    public static CouponCategoryEnum of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(i -> i.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists!"));
    }
}
