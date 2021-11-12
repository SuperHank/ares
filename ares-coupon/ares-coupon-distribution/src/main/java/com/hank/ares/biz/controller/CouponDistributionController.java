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
    public List<Coupon> findCouponsByStatus(@RequestParam("memberCode") String memberCode, @RequestParam("status") Integer status) throws CouponException {
        log.info("Find Coupons By Status: {}, {}", memberCode, status);
        return couponService.findCouponsByStatus(memberCode, status);
    }

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     */
    @GetMapping("/template")
    public List<CouponTemplateDto> findAvailableTemplate(@RequestParam("memberCode") String memberCode) throws CouponException {
        log.info("Find Available Template: {}", memberCode);
        return couponService.findAvailableTemplate(memberCode);
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

    @GetMapping("/get/{memberCode}")
    public List<CouponDto> getTemplateInfo(@PathVariable("memberCode") String memberCode) {
        log.info("Find Coupons By MemberCode :{}", memberCode);
        return couponService.getByUserId(memberCode);
    }
}