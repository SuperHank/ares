package com.hank.ares.api.service;

import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.coupon.CouponTemplateDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ICouponTemplateApiService {
    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     *
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    CouponTemplateDto getById(Integer id);

    /**
     * 查找所有可用的优惠券模板
     *
     * @return {@link CouponTemplateDto}s
     */
    List<CouponTemplateDto> getAllUsableTemplate();

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     *
     * @param ids 模板 ids
     * @return Map<key: 模板 id ， value: CouponTemplateSDK>
     */
    Map<Integer, CouponTemplateDto> getByIds(Collection<Integer> ids);
}
