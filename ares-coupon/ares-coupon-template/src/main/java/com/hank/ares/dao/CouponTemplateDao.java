package com.hank.ares.dao;

import com.hank.ares.model.CouponTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 优惠券模板表 Mapper 接口
 *
 * @author shih
 * @since 2021-06-23
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {

    /**
     * <h2>根据模板名称查询模板</h2>
     * where name = ...
     */
    CouponTemplate findByName(String name);

    /**
     * <h2>根据 available 和 expired 标记查找模板记录</h2>
     * where available = ... and expired = ...
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    /**
     * <h2>根据 expired 标记查找模板记录</h2>
     * where expired = ...
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);

    @Modifying
    @Query("select couponTemplate from CouponTemplate couponTemplate where couponTemplate.id = ?1")
    CouponTemplate findById(@Param("id") Long id);

}
