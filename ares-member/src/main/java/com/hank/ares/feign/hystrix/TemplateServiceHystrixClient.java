package com.hank.ares.feign.hystrix;

import com.hank.ares.feign.TemplateServiceFeignClient;
import com.hank.ares.model.CommonResponse;
import com.hank.ares.model.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TemplateServiceHystrixClient implements TemplateServiceFeignClient {

    @Override
    public CommonResponse<CouponTemplateSDK> getById(Integer id) {
        return null;
    }

    @Override
    public CommonResponse<List<CouponTemplateSDK>> getAllUsableTemplate() {
        return null;
    }

    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> getByIds(Collection<Integer> ids) {
        return null;
    }
}
