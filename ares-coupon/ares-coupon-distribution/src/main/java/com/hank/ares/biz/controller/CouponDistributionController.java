package com.hank.ares.biz.controller;


import com.alibaba.fastjson.JSON;
import com.hank.ares.biz.service.ICouponService;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.CouponDto;
import com.hank.ares.model.CouponTemplateDto;
import com.hank.ares.model.dto.req.AcquireTemplateReqDto;
import com.hank.ares.model.settlement.SettlementDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券(用户领取的记录) 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("/coupon")
public class CouponDistributionController {

    @Autowired
    private ICouponService couponService;

    /**
     * 根据用户 id 和优惠券状态查找用户优惠券记录
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(@RequestParam("userId") Integer userId, @RequestParam("status") Integer status) throws CouponException {
        log.info("Find Coupons By Status: {}, {}", userId, status);
        return couponService.findCouponsByStatus(userId, status);
    }

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     */
    @GetMapping("/template")
    public List<CouponTemplateDto> findAvailableTemplate(@RequestParam("userId") Integer userId) throws CouponException {
        log.info("Find Available Template: {}", userId);
        return couponService.findAvailableTemplate(userId);
    }

    /**
     * 用户领取优惠券
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateReqDto request) throws CouponException {
        log.info("Acquire Template: {}", JSON.toJSONString(request));
        return couponService.acquireTemplate(request);
    }

    /**
     * 结算(核销)优惠券
     */
    @PostMapping("/settlement")
    public SettlementDto settlement(@RequestBody SettlementDto info) throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(info));
        return couponService.settlement(info);
    }

    @GetMapping("/get/{userId}")
    public List<CouponDto> getTemplateInfo(@PathVariable("userId") Integer userId) {
        log.info("Find Coupons By UserID :{}", userId);
        return couponService.getByUserId(userId);
    }
}