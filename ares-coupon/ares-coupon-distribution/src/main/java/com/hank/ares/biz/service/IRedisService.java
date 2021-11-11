package com.hank.ares.biz.service;

import com.hank.ares.enums.coupon.CouponStatusEnum;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.Coupon;

import java.util.List;

/**
 * Redis相关的操作服务接口定义
 * 1. 用户的三个状态优惠券Cache相关操作
 * 2. 优惠券模版生成的优惠券码Cache操作
 */
public interface IRedisService {

    /**
     * 根据userId和状态查询缓存的优惠券列表数据
     *
     * @param userId 用户ID
     * @param status 优惠券状态 {@link CouponStatusEnum}
     * @return {@link Coupon}s 注意，可能会返回null，代表从没有过记录
     */
    List<Coupon> getCachedCoupons(Long userId, Integer status);

    /**
     * 保存空的优惠券列表到缓存中
     * 防止缓存穿透
     *
     * @param userId 用户 id
     * @param status 优惠券状态列表
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * 尝试从 Cache 中获取一个优惠券码
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * 将优惠券保存到 Cache 中
     *
     * @param userId  用户 id
     * @param coupons {@link Coupon}s
     * @param status  优惠券状态
     * @return 保存成功的个数
     */
    void addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException;
}
