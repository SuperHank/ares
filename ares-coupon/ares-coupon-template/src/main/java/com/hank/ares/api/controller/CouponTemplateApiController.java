package com.hank.ares.api.controller;


import com.alibaba.fastjson.JSON;
import com.hank.ares.api.service.ICouponTemplateApiService;
import com.hank.ares.model.CouponTemplateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板SDK controller
 */
@Slf4j
@RestController
@RequestMapping("/template")
public class CouponTemplateApiController {

    @Autowired
    private ICouponTemplateApiService couponTemplateSdkService;

    /**
     * 根据 模版ID 查询模版详情
     */
    @GetMapping("/id/{id}")
    public CouponTemplateDto getById(@PathVariable("id") Integer id) {
        log.info("Get Template Info For:{}", id);
        return couponTemplateSdkService.getById(id);
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    @GetMapping("/ids")
    public Map<Integer, CouponTemplateDto> getByIds(@RequestParam("ids") Collection<Integer> ids) {
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return couponTemplateSdkService.getByIds(ids);
    }

    /**
     * 查询所有可用的优惠券模版
     */
    @GetMapping("/status/usable")
    public List<CouponTemplateDto> getAllUsable() {
        log.info("Get All Usable Template");
        return couponTemplateSdkService.getAllUsableTemplate();
    }

    /**
     * 获取 templateCode 到 CouponTemplateSDK的映射
     */
    @GetMapping("/templateCode/{templateCode}")
    public CouponTemplateDto getByTemplateCode(@PathVariable("templateCode") String templateCode) {
        log.info("Get Template Info For :{}", templateCode);
        return couponTemplateSdkService.getByTemplateCode(templateCode);
    }

    /**
     * 获取 templateCode 到 CouponTemplateSDK的映射
     */
    @GetMapping("/templateCodes")
    public Map<String, CouponTemplateDto> getByTemplateCodes(@RequestParam("templateCodes") Collection<String> templateCodes) {
        log.info("FindTemplateCodes2TemplateSDK: {}", JSON.toJSONString(templateCodes));
        return couponTemplateSdkService.getByTemplateCodes(templateCodes);
    }
}
