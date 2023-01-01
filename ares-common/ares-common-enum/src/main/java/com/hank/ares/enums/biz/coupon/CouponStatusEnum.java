package com.hank.ares.enums.biz.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponStatusEnum {
    USABLE(1, "可用的"),
    USED(2, "已使用的"),
    EXPIRED(3, "过期的(未被使用的)");

    /**
     * 优惠券状态编码
     */
    private final Integer status;
    /**
     * 优惠券状态描述信息
     */
    private final String description;


    /**
     * 根据 status 获取到 CouponStatus
     */
    public static CouponStatusEnum of(Integer status) {

        Objects.requireNonNull(status);

        return Stream.of(values())
                .filter(bean -> bean.status.equals(status))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(status + " not exists")
                );
    }
}
