package com.hank.ares.model.coupon.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto implements Serializable {
    private Integer id;
    private String templateCode;
    private String memberCode;
    private String couponCode;
    private Date assignTime;
    private Integer status;
}
