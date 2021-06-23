package com.hank.ares.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 优惠券(用户领取的记录)
 * </p>
 *
 * @author shih
 * @since 2021-06-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("coupon")
@ApiModel(value="Coupon对象", description="优惠券(用户领取的记录)")
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
    private LocalDateTime assignTime;

    @ApiModelProperty(value = "优惠券的状态")
    private Integer status;


}
