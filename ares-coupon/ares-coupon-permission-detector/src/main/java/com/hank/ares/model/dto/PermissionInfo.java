package com.hank.ares.model.dto;

import lombok.Data;

/**
 * 接口权限信息组装类定义
 */
@Data
public class PermissionInfo {

    /**
     * Controller 的 URL
     */
    private String url;

    /**
     * 方法类型
     */
    private String method;

    /**
     * 是否是只读的
     */
    private Boolean isRead;

    /**
     * 方法描述信息
     */
    private String description;

    /**
     * 扩展属性
     */
    private String extra;
}
