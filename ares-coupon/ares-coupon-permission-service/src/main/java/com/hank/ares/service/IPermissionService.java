package com.hank.ares.service;

/**
 * 权限校验功能服务接口实现
 */
public interface IPermissionService {

    /**
     * 用户访问接口权限校验
     *
     * @param userId     用户 id
     * @param uri        访问 uri
     * @param httpMethod 请求类型
     * @return true/false
     */
    boolean checkPermission(Integer userId, String uri, String httpMethod);
}
