package com.hank.ares.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Redis Key 前缀定义
 */
@Getter
@AllArgsConstructor
public enum RedisPrefixEnum {

    /**
     * 优惠券码 key 前缀
     */
    COUPON_TEMPLATE("ares_coupon_template_code_"),
    /**
     * 用户当前所有可用的优惠券 key 前缀
     */
    USER_COUPON_USABLE("ares_user_coupon_usable_"),
    /**
     * 用户当前所有已使用的优惠券 key 前缀
     */
    USER_COUPON_USED("ares_user_coupon_used_"),
    /**
     * 用户当前所有已过期的优惠券 key 前缀
     */
    USER_COUPON_EXPIRED("ares_user_coupon_expired_"),
    ;

    private final String prefix;
}
