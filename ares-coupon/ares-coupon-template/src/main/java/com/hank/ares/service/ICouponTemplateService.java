package com.hank.ares.service;

import com.hank.ares.exception.CouponException;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.dto.req.CreateTemplateReqDto;

/**
 * 优惠券模板表 服务类
 */
public interface ICouponTemplateService {
    /**
     * 创建优惠券模板
     *
     * @param reqDto {@link CreateTemplateReqDto} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    CouponTemplate buildTemplate(CreateTemplateReqDto reqDto) throws CouponException;
}
