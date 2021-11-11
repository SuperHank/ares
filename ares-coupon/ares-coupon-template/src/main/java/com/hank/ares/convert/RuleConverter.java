package com.hank.ares.convert;

import com.alibaba.fastjson.JSON;
import com.hank.ares.model.coupon.TemplateRuleDto;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 优惠券分类枚举属性转换器
 * AttributeConverter<X, Y>
 * X: 是实体属性的类型
 * Y: 是数据库字段的类型
 */
@Converter
public class RuleConverter implements AttributeConverter<TemplateRuleDto, String> {

    @Override
    public String convertToDatabaseColumn(TemplateRuleDto rule) {
        return JSON.toJSONString(rule);
    }

    @Override
    public TemplateRuleDto convertToEntityAttribute(String rule) {
        return JSON.parseObject(rule, TemplateRuleDto.class);
    }
}