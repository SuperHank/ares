package com.hank.ares.model.dto;

import com.hank.ares.model.BaseReqDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SingleAddReqDto extends BaseReqDto {
    @ApiModelProperty("路由ID组")
    private String id;
    @ApiModelProperty("转发路径")
    private String path;
    @ApiModelProperty("过滤路由")
    private String uri;

    @Override
    protected boolean validate() {
        return false;
    }
}
