package com.hank.ares.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.coupon.template.CouponDto;
import com.hank.ares.model.coupon.template.CouponTemplateDto;
import com.hank.ares.model.dto.req.AcquireTemplateReqDto;
import com.hank.ares.model.settlement.SettlementDto;

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
     * @param memberCode 用户 id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     */
    List<Coupon> findCouponsByStatus(String memberCode, Integer status) throws CouponException;

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     *
     * @param memberCode 用户 id
     * @return {@link CouponTemplateDto}s
     */
    List<CouponTemplateDto> findAvailableTemplate(String memberCode) throws CouponException;

    /**
     * 结算(核销)优惠券
     *
     * @param info {@link SettlementDto}
     * @return {@link SettlementDto}
     */
    SettlementDto settlement(SettlementDto info) throws CouponException;

    /**
     * 查询用户所有的优惠券
     *
     * @param memberCode
     * @return
     */
    List<CouponDto> getByUserId(String memberCode);
}
