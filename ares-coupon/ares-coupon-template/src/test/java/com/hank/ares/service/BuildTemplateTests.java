package com.hank.ares.service;

import com.alibaba.fastjson.JSON;
import com.hank.ares.enums.CouponCategory;
import com.hank.ares.enums.DistributeTarget;
import com.hank.ares.enums.PeriodType;
import com.hank.ares.enums.ProductLine;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.TemplateRule;
import com.hank.ares.model.dto.req.CreateTemplateReqDto;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * 构造优惠券模版服务测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateTests {
    @Autowired
    private ICouponTemplateService templateService;


    @Test
    public void testBuildTemplate() throws CouponException {
        templateService.buildTemplate(fakeTemplateRequest());
    }

    /**
     * fake TemplateRequest
     */
    private CreateTemplateReqDto fakeTemplateRequest() {

        CreateTemplateReqDto request = new CreateTemplateReqDto();
        request.setName("优惠券模板-" + new Date().getTime());
        request.setLogo("http://www.imooc.com");
        request.setDesc("这是一张优惠券模板");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.DAMAO.getCode());
        request.setCount(10000);
        request.setUserId(10001L);  // fake user id
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(),
                1, DateUtils.addDays(new Date(), 60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "安徽省", "桐城市",
                JSON.toJSONString(Arrays.asList("文娱", "家居"))
        ));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(rule);

        return request;
    }
}
