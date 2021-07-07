package com.hank.ares.controller;


import com.alibaba.fastjson.JSON;
import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.CouponTemplateSDK;
import com.hank.ares.model.dto.req.CreateTemplateReqDto;
import com.hank.ares.service.ICouponTemplateBaseService;
import com.hank.ares.service.ICouponTemplateService;
import com.hank.ares.util.ExceptionThen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板
 */
@RestController
@Slf4j
public class CouponTemplateController {

    @Autowired
    private ICouponTemplateService couponTemplateService;
    @Autowired
    private ICouponTemplateBaseService couponTemplateBaseService;

    /**
     * 构造优惠券模版
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody CreateTemplateReqDto reqDto) throws CouponException {
        log.info("Build Template ReqDto: {}", JSON.toJSONString(reqDto));

        ExceptionThen.then(!reqDto.validate(), ResultCode.PARAM_IS_INVALID.getMessage(), new CouponException("BuildTemplate Param Is Not Valid!"));

        CouponTemplate respDto = couponTemplateService.buildTemplate(reqDto);

        log.info("Build Template RespDto: {}", JSON.toJSONString(respDto));

        return respDto;
    }

    /**
     * 根据ID查询模版详情
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) {
        log.info("Build Template Info For:{}", id);
        return couponTemplateBaseService.buildTemplateInfo(id);
    }

    /**
     * 查询所有可用的优惠券模版
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template");
        return couponTemplateBaseService.findAllUsableTemplate();
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids) {
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return couponTemplateBaseService.findIds2TemplateSDK(ids);
    }
}
