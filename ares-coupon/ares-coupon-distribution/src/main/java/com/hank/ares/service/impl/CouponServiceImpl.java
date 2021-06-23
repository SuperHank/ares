package com.hank.ares.service.impl;

import com.hank.ares.model.Coupon;
import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.service.ICouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优惠券(用户领取的记录) 服务实现类
 * </p>
 *
 * @author shih
 * @since 2021-06-23
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

}
