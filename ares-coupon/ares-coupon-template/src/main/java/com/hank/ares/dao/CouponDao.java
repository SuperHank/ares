package com.hank.ares.dao;

import com.hank.ares.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 优惠券模板表 Mapper 接口
 */
public interface CouponDao extends JpaRepository<Coupon, Long> {
    /**
     * 根据模板名称查询模板
     * where name = ...
     */
    List<Coupon> findByUserId(Integer userId);
}
