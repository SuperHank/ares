package com.hank.ares.constants;

import com.hank.ares.enums.CouponCategory;
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
public enum RuleFlag {

    // 单类别优惠券定义
    MANJIAN(Stream.of(CouponCategory.MANJIAN).collect(Collectors.toList()), "满减券的计算规则"),
    ZHEKOU(Stream.of(CouponCategory.ZHEKOU).collect(Collectors.toList()), "折扣券的计算规则"),
    LIJIAN(Stream.of(CouponCategory.LIJIAN).collect(Collectors.toList()), "立减券的计算规则"),

    // 多类别优惠券定义
    MANJIAN_ZHEKOU(Stream.of(CouponCategory.MANJIAN, CouponCategory.ZHEKOU).collect(Collectors.toList()), "满减券 + 折扣券的计算规则");
    // TODO 更多优惠券类别的组合

    private final List<CouponCategory> couponCategorys;
    /**
     * 规则描述
     */
    private final String description;

    public static RuleFlag of(List<CouponCategory> couponCategories) {
        Objects.requireNonNull(couponCategories);
        return Stream.of(values())
                .filter(i -> CollectionUtils.isEqualCollection(i.getCouponCategorys(), couponCategories))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(couponCategories + " not exists!"));
    }
}
