package com.hank.ares.controller;


import com.alibaba.fastjson.JSON;
import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.dto.req.CreateTemplateReqDto;
import com.hank.ares.service.ICouponTemplateService;
import com.hank.ares.util.ExceptionThen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 优惠券模板表 前端控制器
 * </p>
 *
 * @author shih
 * @since 2021-06-23
 */
@RestController
@RequestMapping("/ares/coupon-template")
@Slf4j
public class CouponTemplateController {

    @Autowired
    private ICouponTemplateService couponTemplateService;

    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody CreateTemplateReqDto reqDto) throws CouponException {
        log.info("Build Template ReqDto: {}", JSON.toJSONString(reqDto));

        ExceptionThen.then(!reqDto.validate(), ResultCode.PARAM_IS_INVALID.getMessage(), new CouponException("BuildTemplate Param Is Not Valid!"));

        CouponTemplate respDto = couponTemplateService.buildTemplate(reqDto);

        log.info("Build Template RespDto: {}", JSON.toJSONString(respDto));

        return respDto;
    }
}
