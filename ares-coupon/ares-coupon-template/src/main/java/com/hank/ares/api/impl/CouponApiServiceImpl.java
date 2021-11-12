package com.hank.ares.api.impl;

import com.hank.ares.api.service.ICouponApiService;
import com.hank.ares.dao.CouponDao;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.CouponDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CouponApiServiceImpl implements ICouponApiService {

    @Autowired
    private CouponDao couponDao;


    @Override
    public List<CouponDto> getAllCouponByUserId(String  memberCode) {
        List<Coupon> coupons = couponDao.findByMemberCode(memberCode);
        return coupons.stream().map(this::coupon2CouponDto).collect(Collectors.toList());
    }

    /**
     * 将 CouponDto 转换为 coupon
     */
    private CouponDto coupon2CouponDto(Coupon coupon) {
        return new CouponDto(
                coupon.getId(),
                coupon.getTemplateCode(),
                coupon.getMemberCode(),
                coupon.getCouponCode(),
                coupon.getAssignTime(),
                coupon.getStatus()
        );
    }
}
