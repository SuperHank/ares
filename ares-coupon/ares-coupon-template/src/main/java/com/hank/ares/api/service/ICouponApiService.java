package com.hank.ares.api.service;


import com.hank.ares.model.coupon.template.CouponDto;

import java.util.List;

public interface ICouponApiService {
    /**
     * 获取用户所有的优惠券
     *
     * @param userId
     * @return
     */
    List<CouponDto> getAllCouponByUserId(String memberCode);
}
