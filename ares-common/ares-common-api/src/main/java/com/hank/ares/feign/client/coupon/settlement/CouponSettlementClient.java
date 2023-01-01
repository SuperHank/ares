package com.hank.ares.feign.client.coupon.settlement;

import com.hank.ares.model.settlement.SettlementDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "ares-coupon-settlement")
public interface CouponSettlementClient {
    /**
     * 优惠券规则计算
     */
    @PostMapping(value = "/coupon-settlement/settlement/compute")
    SettlementDto computeRule(@RequestBody SettlementDto settlement);
}
