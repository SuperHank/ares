package com.hank.ares.api.controller;

import com.hank.ares.api.service.ICouponApiService;
import com.hank.ares.model.CouponDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class CouponApiController {

    @Autowired
    private ICouponApiService couponApiService;

    /**
     * 根据ID查询模版详情
     */
    @GetMapping("/coupon/get/{userId}")
    public List<CouponDto> getTemplateInfo(@PathVariable("userId") Integer userId) {
        log.info("Get Template Info For:{}", userId);
        return couponApiService.getAllCouponByUserId(userId);
    }
}
