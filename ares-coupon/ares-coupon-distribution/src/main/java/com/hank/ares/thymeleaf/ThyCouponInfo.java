package com.hank.ares.thymeleaf;

import com.hank.ares.model.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;

/**
 * 用户优惠券信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ThyCouponInfo {

    private Integer id;

    /**
     * 关联优惠券模板的主键
     */
    private String templateCode;

    /**
     * 领取用户
     */
    private String memberCode;

    /**
     * 优惠券码
     */
    private String couponCode;

    /**
     * 领取时间
     */
    private String assignTime;

    /**
     * 优惠券状态
     */
    private Integer status;

    /**
     * 优惠券实体转换为 ThyCouponInfo
     */
    static ThyCouponInfo to(Coupon coupon) {

        ThyCouponInfo info = new ThyCouponInfo();
        info.setId(coupon.getId());
        info.setTemplateCode(coupon.getTemplateCode());
        info.setMemberCode(coupon.getMemberCode());
        info.setCouponCode(coupon.getCouponCode());
        info.setAssignTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(coupon.getAssignTime()));
        info.setStatus(coupon.getStatus());

        return info;
    }
}
