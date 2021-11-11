package com.hank.ares.model;

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
    private Integer templateId;
    private Integer userId;
    private String couponCode;
    private Date assignTime;
    private Integer status;
}
