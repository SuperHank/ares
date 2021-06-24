package com.hank.ares.model.dto.req;

import com.hank.ares.enums.CouponCategory;
import com.hank.ares.enums.DistributeTarget;
import com.hank.ares.enums.ProductLine;
import com.hank.ares.model.BaseReqDto;
import com.hank.ares.model.TemplateRule;
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
    private Long userId;

    /**
     * 目标用户
     */
    private Integer target;

    /**
     * 优惠券规则
     */
    private TemplateRule rule;

    /**
     * 校验对象的合法性
     */
    @Override
    public boolean validate() {

        boolean stringValid = StringUtils.isNotEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);
        boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        boolean numValid = count > 0 && userId > 0;

        return stringValid && enumValid && numValid && rule.validate();
    }
}
