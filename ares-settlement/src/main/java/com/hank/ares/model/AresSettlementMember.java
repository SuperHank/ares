package com.hank.ares.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 清分会员表
 * </p>
 *
 * @author shih
 * @since 2021-02-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="AresSettlementMember对象", description="清分会员表")
public class AresSettlementMember extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "会员名称")
    @TableField("MEMBER_NAME")
    private String memberName;

    @ApiModelProperty(value = "会员编号")
    @TableField("MEMBER_CODE")
    private String memberCode;

    @ApiModelProperty(value = "会员性别")
    @TableField("MEMBER_GENDER")
    private String memberGender;

    @ApiModelProperty(value = "状态（-1-无效， 1-有效）")
    @TableField("STATUS")
    private Integer status;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER")
    private String creater;

    @ApiModelProperty(value = "修改人")
    @TableField("EDITOR")
    private String editor;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;


}
