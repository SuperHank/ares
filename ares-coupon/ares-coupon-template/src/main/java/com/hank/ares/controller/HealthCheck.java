package com.hank.ares.controller;

import com.hank.ares.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class HealthCheck {


    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private Registration registration;

    /**
     * 健康检查接口
     *
     * @return
     */
    @GetMapping("/health")
    public String health() {
        log.debug("view health api");
        return "CouoponTemplate IS OK!";
    }


    /**
     * 异常测试接口
     * 验证统一异常处理是否正常
     *
     * @return
     * @throws CouponException
     */
    @GetMapping("/exception")
    public String exception() throws CouponException {
        log.debug("view exception api");
        throw new CouponException("CouponTemplate Has Some Problem");
    }

    /**
     * 获取Eureka Server上的微服务元信息
     *
     * @return
     */
    @GetMapping("/info")
    public List<Map<String, Object>> info() {
        List<ServiceInstance> instances = discoveryClient.getInstances(registration.getServiceId());

        List<Map<String, Object>> result = new ArrayList<>(instances.size());

        instances.forEach(i -> {
            Map<String, Object> info = new HashMap<>();
            info.put("serviceId", i.getServiceId());
            info.put("instanceId", i.getInstanceId());
            info.put("port", i.getPort());

            result.add(info);
        });

        return result;
    }
}
