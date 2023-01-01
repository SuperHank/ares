package com.hank.ares.enums.biz.permission;

import com.hank.ares.enums.biz.coupon.CouponCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 规则类型枚举定义
 */
@Getter
@AllArgsConstructor
public enum RuleFlagEnum {

    // 单类别优惠券定义
    MANJIAN(Stream.of(CouponCategoryEnum.MANJIAN).collect(Collectors.toList()), "满减券计算规则"),
    ZHEKOU(Stream.of(CouponCategoryEnum.ZHEKOU).collect(Collectors.toList()), "折扣券计算规则"),
    LIJIAN(Stream.of(CouponCategoryEnum.LIJIAN).collect(Collectors.toList()), "立减券计算规则"),

    // 多类别优惠券定义
    MANJIAN_ZHEKOU(Stream.of(CouponCategoryEnum.MANJIAN, CouponCategoryEnum.ZHEKOU).collect(Collectors.toList()), "满减券 + 折扣券计算规则");
    // TODO 更多优惠券类别的组合

    private final List<CouponCategoryEnum> couponCategoryEnums;
    /**
     * 规则描述
     */
    private final String description;

    public static RuleFlagEnum of(List<CouponCategoryEnum> couponCategories) {
        Objects.requireNonNull(couponCategories);
        return Stream.of(values())
                .filter(i -> CollectionUtils.isEqualCollection(i.getCouponCategoryEnums(), couponCategories))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(couponCategories + " not exists!"));
    }
}
