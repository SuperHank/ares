package com.hank.ares.client.coupon;

import com.hank.ares.model.CouponDto;
import com.hank.ares.model.CouponTemplateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(value = "ares-coupon-template")
public interface CuoponTemplateClient {
    /**
     * 获取模版id 到 CouponTemplateSDK的映射
     */
    @GetMapping("/coupon-template/template/id/{id}")
    CouponTemplateDto getTemplateById(@PathVariable("id") Integer id);

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    @GetMapping("/coupon-template/template/ids")
    Map<Integer, CouponTemplateDto> getTemplateByIds(@RequestParam("ids") Collection<Integer> ids);

    /**
     * 查找所有可用的优惠券模板
     */
    @GetMapping("/coupon-template/template/status/usable")
    List<CouponTemplateDto> getAllUsableTemplate();

    /**
     * 获取 templateCode 到 CouponTemplateSDK的映射
     */
    @GetMapping("/coupon-template/template/templateCode/{templateCode}")
    CouponTemplateDto getTemplateByTemplateCode(@PathVariable("templateCode") String templateCode);

    /**
     * 获取 templateCode 到 CouponTemplateSDK的映射
     */
    @GetMapping("/coupon-template/template/templateCodes")
    Map<String, CouponTemplateDto> getTemplateByTemplateCodes(@RequestParam("templateCodes") Collection<String> templateCodes);

    //------------------------------------------------以上为template服务--------------------------------------------------
    //------------------------------------------------以下为coupon服务----------------------------------------------------

    /**
     * 查询用户所有的优惠券
     */
    @GetMapping("/coupon-template/coupon/memberCode/{memberCode}")
    List<CouponDto> getCouponByMemberCode(@PathVariable("memberCode") String memberCode);
}
