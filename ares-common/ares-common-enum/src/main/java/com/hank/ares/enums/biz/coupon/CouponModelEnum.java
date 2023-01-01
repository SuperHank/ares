package com.hank.ares.enums.biz.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优惠券微服务访问路径
 */
@Getter
@AllArgsConstructor
public enum CouponModelEnum {

    TEMPLATE("ares-coupon-template", "coupon-template"),
    DISTRIBUTION("ares-coupon-distribution", "ares-distribution"),
    SETTLEMENT("ares-coupon-settlement", "ares-settlement"),
    ;

    private final String serviceName;
    private final String contextPath;
}
