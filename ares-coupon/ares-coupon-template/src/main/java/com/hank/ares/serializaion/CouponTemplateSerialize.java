package com.hank.ares.serializaion;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hank.ares.model.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 优惠券模板实体类自定义序列化器
 */
public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {

    @Override
    public void serialize(CouponTemplate template,
                          JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {

        // 开始序列化对象
        generator.writeStartObject();

        generator.writeStringField("id", template.getId().toString());
        generator.writeStringField("name", template.getName());
        generator.writeStringField("logo", template.getLogo());
        generator.writeStringField("desc", template.getIntro());
        generator.writeStringField("category",
                template.getCategory().getDescription());
        generator.writeStringField("productLine",
                template.getProductLine().getDescription());
        generator.writeStringField("count", template.getCouponCount().toString());
        generator.writeStringField("createTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(template.getCreateTime()));
        generator.writeStringField("userId", template.getUserId().toString());
        generator.writeStringField("key",
                template.getTemplateKey() + String.format("%04d", template.getId()));
        generator.writeStringField("target",
                template.getTarget().getDescription());
        generator.writeStringField("rule",
                JSON.toJSONString(template.getRule()));

        // 结束序列化对象
        generator.writeEndObject();
    }
}
