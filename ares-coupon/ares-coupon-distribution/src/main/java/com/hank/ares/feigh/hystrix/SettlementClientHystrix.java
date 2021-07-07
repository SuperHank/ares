package com.hank.ares.feigh.hystrix;

import com.hank.ares.exception.CouponException;
import com.hank.ares.feigh.SettlementClient;
import com.hank.ares.model.CommonResponse;
import com.hank.ares.model.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 优惠券模板 Feign 接口的熔断降级策略
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {

    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule request error");

        settlement.setEmploy(false);
        settlement.setCost(-1.0);

        return new CommonResponse<>(-1, "[eureka-client-coupon-settlement] request error", settlement);
    }
}
