package com.hank.ares.model.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AcquireTemplateReqDto {
    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * 优惠券模板信息
     */
    private Integer templateSDKId;
}
