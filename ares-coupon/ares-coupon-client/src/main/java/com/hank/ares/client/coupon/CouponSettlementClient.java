package com.hank.ares.client.coupon;

import com.hank.ares.model.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "ares-coupon-settlement")
public interface CouponSettlementClient {
    /**
     * 优惠券规则计算
     */
    @PostMapping(value = "/coupon-settlement/settlement/compute")
    SettlementInfo computeRule(@RequestBody SettlementInfo settlement);
}
