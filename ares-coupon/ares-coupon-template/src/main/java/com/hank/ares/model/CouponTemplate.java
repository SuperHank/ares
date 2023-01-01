package com.hank.ares.model;

import com.hank.ares.convert.CouponCategoryConverter;
import com.hank.ares.convert.DistributeTargetConverter;
import com.hank.ares.convert.ProductLineConverter;
import com.hank.ares.convert.RuleConverter;
import com.hank.ares.enums.biz.coupon.CouponCategoryEnum;
import com.hank.ares.enums.biz.coupon.DistributeTargetEnum;
import com.hank.ares.enums.biz.coupon.ProductLineEnum;
import com.hank.ares.model.coupon.TemplateRuleDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 优惠券模板表
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "CouponTemplate对象", description = "优惠券模板表")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplate {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增主键")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(value = "模版编号")
    // 模版编号
    @Column(name = "template_code", nullable = false)
    private String templateCode;

    @ApiModelProperty(value = "是否是可用状态; true: 可用, false: 不可用")
    // 提前关闭优惠券领取
    @Column(name = "available", nullable = false)
    private Boolean available;

    @ApiModelProperty(value = "是否过期; true: 是, false: 否")
    // 自然过期
    @Column(name = "expired", nullable = false)
    private Boolean expired;

    @ApiModelProperty(value = "优惠券名称")
    @Column(name = "name", nullable = false)
    private String name;

    @ApiModelProperty(value = "优惠券 logo")
    @Column(name = "logo", nullable = false)
    private String logo;

    @ApiModelProperty(value = "优惠券描述")
    @Column(name = "intro", nullable = false)
    private String intro;

    @ApiModelProperty(value = "优惠券分类")
    @Column(name = "category", nullable = false)
    @Convert(converter = CouponCategoryConverter.class)
    private CouponCategoryEnum category;

    @ApiModelProperty(value = "产品线")
    @Column(name = "product_line", nullable = false)
    @Convert(converter = ProductLineConverter.class)
    private ProductLineEnum productLineEnum;

    @ApiModelProperty(value = "总数")
    @Column(name = "coupon_count", nullable = false)
    private Integer couponCount;

    @ApiModelProperty(value = "创建时间")
    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @ApiModelProperty(value = "创建用户")
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ApiModelProperty(value = "优惠券模板的编码")
    @Column(name = "template_key", nullable = false)
    private String templateKey;

    @ApiModelProperty(value = "目标用户")
    @Column(name = "target", nullable = false)
    @Convert(converter = DistributeTargetConverter.class)
    private DistributeTargetEnum target;

    @ApiModelProperty(value = "优惠券规则: TemplateRule 的 json 表示")
    @Column(name = "rule", nullable = false)
    @Convert(converter = RuleConverter.class)
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
