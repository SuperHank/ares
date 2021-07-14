package com.hank.ares.thymeleaf;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hank.ares.exception.CouponException;
import com.hank.ares.feigh.TemplateServiceFeignClient;
import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.CouponTemplateSDK;
import com.hank.ares.model.dto.req.AcquireTemplateReqDto;
import com.hank.ares.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 优惠券分发 Controller
 */
@Slf4j
@Controller
@RequestMapping("/distribution/thy")
public class ThyDistributionController {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private UserServiceImpl userService;

    @Qualifier("templateServiceFeignClient")
    @Autowired
    private TemplateServiceFeignClient templateClient;

    /**
     * 当前用户的所有优惠券信息
     */
    @GetMapping("/user/{userId}")
    public String user(@PathVariable Long userId, ModelMap map) {

        log.info("view user: {} coupons.", userId);

        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Coupon> coupons = couponMapper.selectList(queryWrapper);
        List<ThyCouponInfo> infos = coupons.stream()
                .map(ThyCouponInfo::to).collect(Collectors.toList());
        map.addAttribute("coupons", infos);
        map.addAttribute("uid", userId);

        return "user_coupon_list";
    }

    /**
     * 用户可以领取的优惠券模板
     */
    @GetMapping("/template/{userId}")
    public String template(@PathVariable Long userId, ModelMap map) throws CouponException {

        log.info("view user: {} can acquire template.", userId);

        List<CouponTemplateSDK> templateSDKS = userService.findAvailableTemplate(userId);
        List<ThyTemplateInfo> infos = templateSDKS.stream()
                .map(ThyTemplateInfo::to).collect(Collectors.toList());
        infos.forEach(i -> i.setUserId(userId));

        map.addAttribute("templates", infos);

        return "template_list";
    }

    @GetMapping("/template/info")
    public String templateInfo(@RequestParam Long uid, @RequestParam Integer id, ModelMap map) {

        log.info("user view template info: {} -> {}", uid, id);

        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
                Collections.singletonList(id)
        ).getData();

        if (MapUtils.isNotEmpty(id2Template)) {
            ThyTemplateInfo info = ThyTemplateInfo.to(id2Template.get(id));
            info.setUserId(uid);
            map.addAttribute("template", info);
        }

        return "template_detail";
    }

    @GetMapping("/acquire")
    public String acquire(@RequestParam Long uid, @RequestParam Integer tid) throws CouponException {

        log.info("user {} acquire template {}.", uid, tid);

        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
                Collections.singletonList(tid)
        ).getData();
        if (MapUtils.isNotEmpty(id2Template)) {
            log.info("user acquire coupon: {}", JSON.toJSONString(userService.acquireTemplate(
                    new AcquireTemplateReqDto(uid, id2Template.get(tid))
            )));
        }

        return "redirect:/distribution/thy/user/" + uid;
    }
}
