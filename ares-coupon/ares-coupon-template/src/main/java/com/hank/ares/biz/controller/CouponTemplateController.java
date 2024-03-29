package com.hank.ares.biz.controller;


import com.alibaba.fastjson.JSON;
import com.hank.ares.biz.service.ICouponTemplateService;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.dto.req.CreateTemplateReqDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券模板
 */
@Slf4j
@RestController
@RequestMapping("/template")
public class CouponTemplateController {

    @Autowired
    private ICouponTemplateService couponTemplateService;

    /**
     * 构造优惠券模版
     */
    @PostMapping("/build")
    public CouponTemplate buildTemplate(@RequestBody CreateTemplateReqDto reqDto) throws CouponException {
        reqDto.validate();

        log.info("Build Template ReqDto: {}", JSON.toJSONString(reqDto));

        CouponTemplate respDto = couponTemplateService.buildTemplate(reqDto);

        log.info("Build Template RespDto: {}", JSON.toJSONString(respDto));

        return respDto;
    }
}
