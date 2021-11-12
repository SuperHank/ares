package com.hank.ares.api.controller;

import com.hank.ares.api.service.ICouponApiService;
import com.hank.ares.model.CouponDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/coupon")
public class CouponApiController {

    @Autowired
    private ICouponApiService couponApiService;

    /**
     * 根据ID查询模版详情
     */
    @GetMapping("/memberCode/{memberCode}")
    public List<CouponDto> getTemplateInfo(@PathVariable("memberCode") String memberCode) {
        log.info("Get Template Info For:{}", memberCode);
        return couponApiService.getAllCouponByUserId(memberCode);
    }
}
