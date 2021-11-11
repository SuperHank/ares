package com.hank.ares.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.hank.ares.enums.coupon.CouponStatusEnum;
import com.hank.ares.model.coupon.CouponTemplateDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 优惠券(用户领取的记录)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("coupon")
@ApiModel(value = "Coupon对象", description = "优惠券(用户领取的记录)")
@AllArgsConstructor
@NoArgsConstructor
public class Coupon extends Model {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "关联优惠券模板的主键")
    private Integer templateId;

    @ApiModelProperty(value = "领取用户")
    private Long userId;

    @ApiModelProperty(value = "优惠券码")
    private String couponCode;

    @ApiModelProperty(value = "领取时间")
    private Date assignTime;

    @ApiModelProperty(value = "优惠券的状态")
    private Integer status;

    /**
     * 用户优惠券对应的模板信息
     */
    private transient CouponTemplateDto templateSDK;

    /**
     * 返回一个无效的 Coupon 对象
     */
    public static Coupon invalidCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    /**
     * 构造优惠券
     */
    public Coupon(Integer templateId, Long userId, String couponCode, CouponStatusEnum status) {
        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = status.getStatus();
    }
}
