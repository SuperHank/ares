package com.hank.ares.service;


import com.hank.ares.model.CouponTemplate;

/**
 * <h1>异步服务接口定义</h1>
 * Created by Qinyi.
 */
public interface IAsyncService {

    /**
     * 根据模板异步的创建优惠券码
     *
     * @param template {@link CouponTemplate} 优惠券模板实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
