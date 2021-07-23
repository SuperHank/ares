package com.hank.ares.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限校验请求对象定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPermissionReqDto {
    private Long userId;
    private String uri;
    private String httpMethod;
}
