package com.hank.ares;

import com.hank.ares.enums.permission.OperationModeEnum;
import com.hank.ares.feign.IPermissionServiceFeignClient;
import com.hank.ares.model.CommonResponse;
import com.hank.ares.model.dto.CreatePathReqDto;
import com.hank.ares.model.dto.PermissionInfo;
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

    private final IPermissionServiceFeignClient permissionClient;

    private final String serviceName;


    /**
     * 权限注册
     */
    void register(List<PermissionInfo> infoList) {

        if (CollectionUtils.isEmpty(infoList)) {
            return;
        }

        List<CreatePathReqDto.PathInfo> pathInfos = infoList.stream()
                .map(info -> CreatePathReqDto.PathInfo.builder()
                        .pathPattern(info.getUrl())
                        .httpMethod(info.getMethod())
                        .pathName(info.getDescription())
                        .serviceName(serviceName)
                        .opMode(info.getIsRead() ? OperationModeEnum.READ.name() :
                                OperationModeEnum.WRITE.name())
                        .build()
                ).collect(Collectors.toList());

        CommonResponse<List<Integer>> response = permissionClient.createPath(new CreatePathReqDto(pathInfos));

        if (CollectionUtils.isNotEmpty(response.getData())) {
            log.info("register path info: {}", response.getData());
        }
    }
}
