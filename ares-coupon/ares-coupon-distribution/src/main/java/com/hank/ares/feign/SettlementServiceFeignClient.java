package com.hank.ares.feign;

import com.hank.ares.exception.CouponException;
import com.hank.ares.model.CommonResponse;
import com.hank.ares.model.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "ares-coupon-settlement")
public interface SettlementServiceFeignClient {
    /**
     * 优惠券规则计算
     */
    @GetMapping(value = "/coupon-settlement/settlement/compute")
    SettlementInfo computeRule(@RequestBody SettlementInfo settlement) throws CouponException;
}
