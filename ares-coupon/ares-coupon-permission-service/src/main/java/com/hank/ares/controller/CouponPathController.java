package com.hank.ares.controller;


import com.hank.ares.model.coupon.permission.CreatePathReqDto;
import com.hank.ares.service.impl.CouponPathServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 路径信息表 前端控制器
 */
@Slf4j
@RestController
public class CouponPathController {
    @Autowired
    private CouponPathServiceImpl pathService;

    /**
     * 路径创建接口
     */
    @PostMapping("/create/path")
    public void createPath(@RequestBody CreatePathReqDto request) {
        log.info("createPath: {}", request.getPathInfos().size());
        pathService.createPath(request);
    }
}
