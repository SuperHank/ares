package com.hank.ares.service;

import com.hank.ares.exception.CouponException;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ICouponTemplateBaseService {
    /**
     * <h2>根据优惠券模板 id 获取优惠券模板信息</h2>
     *
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     *
     * @return {@link CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     *
     * @param ids 模板 ids
     * @return Map<key: 模板 id ， value: CouponTemplateSDK>
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
