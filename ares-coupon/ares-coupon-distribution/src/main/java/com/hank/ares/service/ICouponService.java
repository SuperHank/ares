package com.hank.ares.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.CouponTemplateSDK;
import com.hank.ares.model.SettlementInfo;
import com.hank.ares.model.dto.req.AcquireTemplateReqDto;

import java.util.List;

/**
 * 优惠券(用户领取的记录) 服务类
 */
public interface ICouponService extends IService<Coupon> {
    /**
     * 用户领取优惠券
     *
     * @param request {@link AcquireTemplateReqDto}
     * @return {@link Coupon}
     */
    Coupon acquireTemplate(AcquireTemplateReqDto request) throws CouponException;

    /**
     * 根据用户 id 和状态查询优惠券记录
     *
     * @param userId 用户 id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     */
    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     *
     * @param userId 用户 id
     * @return {@link CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    /**
     * 结算(核销)优惠券
     *
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;
}
