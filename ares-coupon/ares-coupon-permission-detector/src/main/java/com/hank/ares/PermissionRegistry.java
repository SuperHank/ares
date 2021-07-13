package com.hank.ares;

import com.hank.ares.feign.PermissionFeignClient;
import com.hank.ares.model.CommonResponse;
import com.hank.ares.vo.CreatePathRequest;
import com.hank.ares.vo.PermissionInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限注册组件
 */
@Slf4j
@AllArgsConstructor
public class PermissionRegistry {

    /**
     * 权限服务 SDK 客户端
     */
    private PermissionFeignClient permissionClient;

    /**
     * 服务名称
     */
    private String serviceName;


    /**
     * 权限注册
     */
    boolean register(List<PermissionInfo> infoList) {

        if (CollectionUtils.isEmpty(infoList)) {
            return false;
        }

        List<CreatePathRequest.PathInfo> pathInfos = infoList.stream()
                .map(info -> CreatePathRequest.PathInfo.builder()
                        .pathPattern(info.getUrl())
                        .httpMethod(info.getMethod())
                        .pathName(info.getDescription())
                        .serviceName(serviceName)
                        .opMode(info.getIsRead() ? OpModeEnum.READ.name() :
                                OpModeEnum.WRITE.name())
                        .build()
                ).collect(Collectors.toList());

        CommonResponse<List<Integer>> response = permissionClient.createPath(
                new CreatePathRequest(pathInfos)
        );

        if (!CollectionUtils.isEmpty(response.getData())) {
            log.info("register path info: {}", response.getData());
            return true;
        }

        return false;
    }
}
