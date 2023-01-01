package com.hank.ares.controller;

import com.hank.ares.model.coupon.permission.CheckPermissionReqDto;
import com.hank.ares.service.impl.PermissionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 路径创建与权限校验对外服务接口实现
 */
@RestController
@Slf4j
public class PermissionController {


    @Autowired
    private PermissionServiceImpl permissionService;

    /**
     * 权限校验接口
     */
    @PostMapping("/check/permission")
    public Boolean checkPermission(@RequestBody CheckPermissionReqDto request) {
        log.info("checkPermission for args: {}, {}, {}", request.getUserId(), request.getUri(), request.getHttpMethod());
        return permissionService.checkPermission(request.getUserId(), request.getUri(), request.getHttpMethod());
    }
}
