package com.hank.ares.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.model.Coupon;
import com.hank.ares.service.ICouponService;
import org.springframework.stereotype.Service;

/**
 * 优惠券(用户领取的记录) 服务实现类
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

}
