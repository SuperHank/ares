package com.hank.ares.biz.service;

import com.hank.ares.exception.CouponException;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.CouponTemplateDto;
import com.hank.ares.model.settlement.SettlementDto;
import com.hank.ares.model.dto.req.AcquireTemplateReqDto;

import java.util.List;

public interface IUserService {

    /**
     * 根据用户 id 和状态查询优惠券记录
     *
     * @param userId 用户 id
     * @param status 优惠券状态
     * @return {@link com.hank.ares.model.Coupon}s
     */
    List<Coupon> findCouponsByStatus(Integer userId, Integer status) throws CouponException;

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     *
     * @param userId 用户 id
     * @return {@link CouponTemplateDto}s
     */
    List<CouponTemplateDto> findAvailableTemplate(Integer userId)
            throws CouponException;

    /**
     * 用户领取优惠券
     *
     * @param request {@link AcquireTemplateReqDto}
     * @return {@link Coupon}
     */
    Coupon acquireTemplate(AcquireTemplateReqDto request)
            throws CouponException;

    /**
     * 结算(核销)优惠券
     *
     * @param info {@link SettlementDto}
     * @return {@link SettlementDto}
     */
    SettlementDto settlement(SettlementDto info) throws CouponException;
}
