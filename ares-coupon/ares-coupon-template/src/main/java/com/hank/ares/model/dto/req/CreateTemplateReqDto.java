package com.hank.ares.model.dto.req;

import com.hank.ares.enums.coupon.CouponCategoryEnum;
import com.hank.ares.enums.coupon.DistributeTargetEnum;
import com.hank.ares.enums.coupon.ProductLineEnum;
import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.model.BaseReqDto;
import com.hank.ares.model.coupon.TemplateRuleDto;
import com.hank.ares.util.ExceptionThen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTemplateReqDto extends BaseReqDto {
    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券 logo
     */
    private String logo;

    /**
     * 优惠券描述
     */
    private String desc;

    /**
     * 优惠券分类
     */
    private String category;

    /**
     * 产品线
     */
    private Integer productLine;

    /**
     * 总数
     */
    private Integer count;

    /**
     * 创建用户
     */
    private Integer userId;

    /**
     * 目标用户
     */
    private Integer target;

    /**
     * 优惠券规则
     */
    private TemplateRuleDto rule;

    /**
     * 校验对象的合法性
     */
    @Override
    public void validate() {
        boolean stringValid = StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(logo) && StringUtils.isNotEmpty(desc);

        boolean enumValid = null != CouponCategoryEnum.of(category) && null != ProductLineEnum.of(productLine) && null != DistributeTargetEnum.of(target);

        boolean numValid = count > 0 && userId > 0;

        ExceptionThen.then(!(stringValid && enumValid && numValid), ResultCode.PARAM_IS_INVALID, "BuildTemplate Param Is Not Valid!");
    }
}
