package com.hank.ares.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 优惠券模板表
 * </p>
 *
 * @author shih
 * @since 2021-06-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("coupon_template")
@ApiModel(value = "CouponTemplate对象", description = "优惠券模板表")
public class CouponTemplate extends Model {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "是否是可用状态; true: 可用, false: 不可用")
    private Boolean available;

    @ApiModelProperty(value = "是否过期; true: 是, false: 否")
    private Boolean expired;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠券 logo")
    private String logo;

    @ApiModelProperty(value = "优惠券描述")
    private String intro;

    @ApiModelProperty(value = "优惠券分类")
    private String category;

    @ApiModelProperty(value = "产品线")
    private Integer productLine;

    @ApiModelProperty(value = "总数")
    private Integer couponCount;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建用户")
    private Long userId;

    @ApiModelProperty(value = "优惠券模板的编码")
    private String templateKey;

    @ApiModelProperty(value = "目标用户")
    private Integer target;

    @ApiModelProperty(value = "优惠券规则: TemplateRule 的 json 表示")
    private String rule;


}
