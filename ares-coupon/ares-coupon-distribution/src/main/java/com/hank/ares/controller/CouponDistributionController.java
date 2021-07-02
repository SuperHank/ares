package com.hank.ares.controller;


import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.model.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 优惠券(用户领取的记录) 前端控制器
 * </p>
 *
 * @author shih
 * @since 2021-06-23
 */
@RestController
@RequestMapping("/coupon")
public class CouponDistributionController {

    @Autowired
    private CouponMapper couponMapper;

    @PostMapping("/add")
    public void add() {
        Coupon insertDo = new Coupon();
        couponMapper.insert(insertDo);
    }

    @GetMapping("/get")
    public Coupon get(@RequestParam("id") Integer id) {
        return couponMapper.selectById(id);
    }
}