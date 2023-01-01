package com.hank.ares.convert;

import com.hank.ares.enums.biz.coupon.ProductLineEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 优惠券分类枚举属性转换器
 * AttributeConverter<X, Y>
 * X: 是实体属性的类型
 * Y: 是数据库字段的类型
 */
@Converter
public class ProductLineConverter implements AttributeConverter<ProductLineEnum, Integer> {

    /**
     * 将实体属性X转换为Y存储到数据库中, 插入和更新时执行的动作
     *
     * @param couponCategoryEnum
     * @return
     */
    @Override
    public Integer convertToDatabaseColumn(ProductLineEnum couponCategoryEnum) {
        return couponCategoryEnum.getCode();
    }

    /**
     * 将数据库中的字段Y转换为实体属性X, 查询操作时执行的动作
     *
     * @param code
     * @return
     */
    @Override
    public ProductLineEnum convertToEntityAttribute(Integer code) {
        return ProductLineEnum.of(code);
    }
}
