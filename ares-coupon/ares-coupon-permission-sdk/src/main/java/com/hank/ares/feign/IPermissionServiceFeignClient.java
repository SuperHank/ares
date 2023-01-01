package com.hank.ares.feign;

import com.hank.ares.model.CommonResponse;
import com.hank.ares.model.dto.CheckPermissionReqDto;
import com.hank.ares.model.dto.CreatePathReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 路径创建与权限校验功能 Feign 接口实现
 */
@FeignClient(value = "ares-coupon-permission")
public interface IPermissionServiceFeignClient {

    @PostMapping(value = "/coupon-permission/create/path")
    CommonResponse<List<Integer>> createPath(@RequestBody CreatePathReqDto request);

    @PostMapping(value = "/coupon-permission/check/permission")
    Boolean checkPermission(@RequestBody CheckPermissionReqDto request);
}
