package com.hank.ares.feigh.hystrix;

import com.hank.ares.feigh.TemplateServiceFeignClient;
import com.hank.ares.model.CommonResponse;
import com.hank.ares.model.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 优惠券模板 Feign 接口的熔断降级策略
 */
@Slf4j
@Component
public class TemplateServiceHystrixClient implements TemplateServiceFeignClient {

    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate request error");
        return new CommonResponse<>(-1, "[eureka-client-coupon-template] request error", Collections.emptyList());
    }

    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK request error");
        return new CommonResponse<>(-1, "[eureka-client-coupon-template] request error", new HashMap<>());
    }
}
