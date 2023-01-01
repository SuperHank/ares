package com.hank.ares.model.coupon.template;

import com.hank.ares.enums.biz.coupon.CouponCategoryEnum;
import com.hank.ares.enums.biz.coupon.DistributeTargetEnum;
import com.hank.ares.enums.biz.coupon.ProductLineEnum;
import com.hank.ares.model.coupon.TemplateRuleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 优惠券模板表
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplate {

    private static final long serialVersionUID = 1L;

    private Integer id;

    // 模版编号
    private String templateCode;

    // 提前关闭优惠券领取
    private Boolean available;

    // 自然过期
    private Boolean expired;

    private String name;

    private String logo;

    private String intro;

    private CouponCategoryEnum category;

    private ProductLineEnum productLineEnum;

    private Integer couponCount;

    private Date createTime;

    private Integer userId;

    private String templateKey;

    private DistributeTargetEnum target;

    private TemplateRuleDto rule;

    /**
     * 自定义构造函数
     */
    public CouponTemplate(String templateCode, String name, String logo, String intro, String category,
                          Integer productLine, Integer couponCount, Integer userId,
                          Integer target, TemplateRuleDto rule) {

        this.templateCode = templateCode;
        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.intro = intro;
        this.category = CouponCategoryEnum.of(category);
        this.productLineEnum = ProductLineEnum.of(productLine);
        this.couponCount = couponCount;
        this.userId = userId;
        // 优惠券模板唯一编码 = 4(产品线和类型) + 8(日期: 20190101) + id(扩充为4位)
        this.templateKey = productLine.toString() + category +
                new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.target = DistributeTargetEnum.of(target);
        this.rule = rule;
        this.createTime = new Date();
    }
}
