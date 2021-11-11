package com.hank.ares.feign;

import com.hank.ares.feign.hystrix.TemplateServiceHystrixClient;
import com.hank.ares.model.coupon.CouponTemplateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(value = "ares-coupon-template", fallback = TemplateServiceHystrixClient.class)
public interface TemplateServiceFeignClient {
    /**
     * 获取模版id 到 CouponTemplateSDK的映射
     */
    @GetMapping("/coupon-template/template/sdk/{id}")
    CouponTemplateDto getById(@PathVariable("id") Integer id);

    /**
     * 查找所有可用的优惠券模板
     */
    @GetMapping("/coupon-template/template/sdk/all")
    List<CouponTemplateDto> getAllUsableTemplate();

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    @GetMapping("/coupon-template/template/sdk/infos")
    Map<Integer, CouponTemplateDto> getByIds(@RequestParam("ids") Collection<Integer> ids);
}
