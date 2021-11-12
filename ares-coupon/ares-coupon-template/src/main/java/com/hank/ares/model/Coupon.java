package com.hank.ares.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "Coupon对象", description = "优惠券表")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    @ApiModelProperty(value = "自增主键")
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer Id;

    @ApiModelProperty(value = "优惠券模版ID")
    @Column(name = "template_code", nullable = false)
    private String templateCode;

    @ApiModelProperty(value = "用户ID")
    @Column(name = "member_code", nullable = false)
    private String memberCode;

    @ApiModelProperty(value = "优惠券编号")
    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @ApiModelProperty(value = "领取时间")
    @Column(name = "assign_time", nullable = false)
    private Date assignTime;

    @ApiModelProperty(value = "有效状态 1-有效；-1-无效")
    @Column(name = "status", nullable = false)
    private Integer status;
}
