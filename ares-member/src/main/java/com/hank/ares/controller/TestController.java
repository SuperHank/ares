package com.hank.ares.controller;

import com.hank.ares.feign.TemplateServiceFeignClient;
import com.hank.ares.model.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private TemplateServiceFeignClient templateServiceFeignClient;

    /**
     * 根据ID查询模版详情
     */
    @GetMapping("/template/sdk/{id}")
    public CouponTemplateSDK getTemplateInfo(@PathVariable("id") Integer id) {
        log.info("Get Template Info For:{}", id);
        CouponTemplateSDK data = templateServiceFeignClient.getById(id);
        System.out.println(data);
        return data;
    }

    /**
     * 查询所有可用的优惠券模版
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Get All Usable Template");
        return templateServiceFeignClient.getAllUsableTemplate();
    }
}
