package com.hank.ares.service;

import com.hank.ares.model.dto.SingleAddReqDto;

import java.net.URISyntaxException;

/**
 * 动态路由服务
 */
public interface IDynamicRouteService {
    /**
     * 单条添加
     *
     * @param reqDto
     */
    void singleAdd(SingleAddReqDto reqDto) throws URISyntaxException;
}
