package com.hank.ares.api.controller;


import com.alibaba.fastjson.JSON;
import com.hank.ares.api.service.ICouponTemplateApiService;
import com.hank.ares.model.CouponTemplateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板SDK controller
 */
@Slf4j
@RestController
public class CouponTemplateApiController {

    @Autowired
    private ICouponTemplateApiService couponTemplateSdkService;

    /**
     * 根据ID查询模版详情
     */
    @GetMapping("/template/sdk/{id}")
    public CouponTemplateDto getTemplateInfo(@PathVariable("id") Integer id) {
        log.info("Get Template Info For:{}", id);
        return couponTemplateSdkService.getById(id);
    }

    /**
     * 查询所有可用的优惠券模版
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateDto> findAllUsableTemplate() {
        log.info("Get All Usable Template");
        return couponTemplateSdkService.getAllUsableTemplate();
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateDto> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids) {
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return couponTemplateSdkService.getByIds(ids);
    }
}
