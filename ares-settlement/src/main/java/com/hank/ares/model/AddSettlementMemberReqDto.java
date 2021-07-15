package com.hank.ares.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddSettlementMemberReqDto extends BaseReqDto {
    @ApiModelProperty("会员名称")
    private String memberName;
    @ApiModelProperty("会员性别 M-男；F-女")
    private String memberGender;

    @Override
    public void validate() {
    }
}
