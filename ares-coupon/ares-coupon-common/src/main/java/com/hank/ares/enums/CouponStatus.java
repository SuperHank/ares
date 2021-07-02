package com.hank.ares.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponStatus {
    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期的(未被使用的)", 3);

    /**
     * 优惠券状态描述信息
     */
    private final String description;

    /**
     * 优惠券状态编码
     */
    private final Integer status;

    /**
     * 根据 status 获取到 CouponStatus
     */
    public static CouponStatus of(Integer status) {

        Objects.requireNonNull(status);

        return Stream.of(values())
                .filter(bean -> bean.status.equals(status))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(status + " not exists")
                );
    }
}
