package com.hank.ares.service;

import com.hank.ares.model.dto.SingleAddReqDto;

import java.net.URISyntaxException;

public interface IRouteService {
    /**
     * 单条添加
     *
     * @param reqDto
     */
    void singleAdd(SingleAddReqDto reqDto) throws URISyntaxException;
}
