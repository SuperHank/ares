package com.hank.ares.dao;

import com.hank.ares.model.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * 优惠券模板表 Mapper 接口
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {

    /**
     * 根据模板名称查询模板
     * where name = ...
     */
    CouponTemplate findByName(String name);

    /**
     * 根据 available 和 expired 标记查找模板记录
     * where available = ... and expired = ...
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    /**
     * 根据 expired 标记查找模板记录
     * where expired = ...
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);

    /**
     * 根据 templateCode 查询模版
     *
     * @param templateCode
     * @return
     */
    CouponTemplate findByTemplateCode(String templateCode);


    /**
     * 批量查询模版
     *
     * @param templateCodes
     * @return
     */
    @Query(value = "select * from coupon_template where template_code in (:templateCodes)", nativeQuery = true)
    List<CouponTemplate> findAllByTemplateCode(Collection<String> templateCodes);
}
