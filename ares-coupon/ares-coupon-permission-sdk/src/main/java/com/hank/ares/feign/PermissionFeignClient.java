package com.hank.ares.feign;

import com.hank.ares.model.CommonResponse;
import com.hank.ares.vo.CheckPermissionRequest;
import com.hank.ares.vo.CreatePathRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 路径创建与权限校验功能 Feign 接口实现
 */
@FeignClient(value = "eureka-client-coupon-permission")
public interface PermissionFeignClient {

    @PostMapping(value = "/coupon-permission/create/path")
    CommonResponse<List<Integer>> createPath(@RequestBody CreatePathRequest request);

    @PostMapping(value = "/coupon-permission/check/permission")
    Boolean checkPermission(@RequestBody CheckPermissionRequest request);
}
