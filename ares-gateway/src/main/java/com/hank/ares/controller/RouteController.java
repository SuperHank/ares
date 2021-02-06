package com.hank.ares.controller;

import com.hank.ares.annotations.BizNameAnnotation;
import com.hank.ares.enums.BizNameEnum;
import com.hank.ares.model.dto.SingleAddReqDto;
import com.hank.ares.service.IRouteService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@ApiModel("路由配置")
@RestController
@RequestMapping("/route")
public class RouteController {
    @Autowired
    private IRouteService routeService;

    @ApiOperation("手动添加路由（单条）")
    @PostMapping("singleAdd")
    @BizNameAnnotation(bizName = BizNameEnum.GATEWAY_SINGLE_ADD_ROUTE)
    private void singleAdd(@RequestBody SingleAddReqDto reqDto) throws URISyntaxException {
        routeService.singleAdd(reqDto);
    }
}
