package com.hank.ares.feign.hystrix;

import com.hank.ares.feign.TemplateServiceFeignClient;
import com.hank.ares.model.coupon.CouponTemplateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class TemplateServiceHystrixClient implements TemplateServiceFeignClient {

    @Override
    public CouponTemplateDto getById(Integer id) {
        log.error("[ares-coupon-template] findAllUsableTemplate request error");
        return null;
    }

    @Override
    public List<CouponTemplateDto> getAllUsableTemplate() {
        log.error("[ares-coupon-template] findIds2TemplateSDK request error");
        return Collections.emptyList();
    }

    @Override
    public Map<Integer, CouponTemplateDto> getByIds(Collection<Integer> ids) {
        log.error("[ares-coupon-template] findIds2TemplateSDK request error");
        return new HashMap<Integer, CouponTemplateDto>();
    }
}
