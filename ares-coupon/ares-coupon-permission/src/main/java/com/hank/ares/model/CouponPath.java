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

/**
 * 路径信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("coupon_path")
@ApiModel(value = "CouponPath对象", description = "路径信息表")
public class CouponPath extends Model {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "路径ID, 自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "路径模式	")
    private String pathPattern;

    @ApiModelProperty(value = "http请求类型")
    private String httpMethod;

    @ApiModelProperty(value = "路径描述")
    private String pathName;

    @ApiModelProperty(value = "服务名")
    private String serviceName;

    @ApiModelProperty(value = "操作类型, READ/WRITE")
    private String opMode;


}
