package com.hank.ares.client.coupon;

import com.hank.ares.model.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(value = "ares-coupon-template")
public interface CuoponTemplateClient {
    /**
     * 获取模版id 到 CouponTemplateSDK的映射
     */
    @GetMapping("/coupon-template/template/sdk/{id}")
    CouponTemplateSDK getById(@PathVariable("id") Integer id);

    /**
     * 查找所有可用的优惠券模板
     */
    @GetMapping("/coupon-template/template/sdk/all")
    List<CouponTemplateSDK> getAllUsableTemplate();

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    @GetMapping("/coupon-template/template/sdk/infos")
    Map<Integer, CouponTemplateSDK> getByIds(@RequestParam("ids") Collection<Integer> ids);
}