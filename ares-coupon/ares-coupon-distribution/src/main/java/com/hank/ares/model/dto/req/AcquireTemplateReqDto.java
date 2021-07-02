package com.hank.ares.model.dto.req;

import com.hank.ares.model.CouponTemplateSDK;
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
    private Long userId;

    /**
     * 优惠券模板信息
     */
    private CouponTemplateSDK templateSDK;
}
