package com.hank.ares.model.vo;

import com.hank.ares.enums.biz.coupon.CouponStatusEnum;
import com.hank.ares.enums.biz.coupon.PeriodTypeEnum;
import com.hank.ares.model.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 优惠券分类工具
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponClassify {
    private List<Coupon> usable;
    private List<Coupon> used;
    private List<Coupon> expired;

    public static CouponClassify classify(List<Coupon> coupons) {
        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c -> {
            // 判断优惠券是否过期
            boolean isTimeExpire;
            long curTime = new Date().getTime();

            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(PeriodTypeEnum.REGULAR.getCode())) {
                isTimeExpire = c.getTemplateSDK().getRule().getExpiration().getDeadline() <= curTime;
            } else {
                isTimeExpire = DateUtils.addDays(c.getAssignTime(), c.getTemplateSDK().getRule().getExpiration().getGap()).getTime() <= curTime;
            }

            if (CouponStatusEnum.USED.getStatus().equals(c.getStatus())) {
                used.add(c);
            } else if (CouponStatusEnum.EXPIRED.getStatus().equals(c.getStatus()) || isTimeExpire) {
                expired.add(c);
            } else {
                usable.add(c);
            }
        });
        return new CouponClassify(usable, used, expired);
    }
}
