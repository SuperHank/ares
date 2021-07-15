package com.hank.ares.model;

import com.hank.ares.enums.coupon.PeriodTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {

    // 优惠券过期规则
    private Expiration expiration;
    // 折扣
    private Discount discount;
    // 每个人最多领几张的限制
    private Integer limitation;
    // 适用范围：地域+商品类型
    private Usage usage;
    // 权重（可以和哪些优惠券叠加使用，同一类的优惠券一定不能叠加）：list[] 优惠券的唯一编码
    private String weight;

    /**
     * 校验功能
     */
    public boolean validate() {
        return expiration.validate() && discount.validate() && limitation > 0 && usage.validate() && StringUtils.isNotEmpty(weight);
    }


    /**
     * 有效期限规则
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Expiration {
        /**
         * 有效期规则，对应PeriodType的code字段
         */
        private Integer period;
        /**
         * 有效间隔：只对变动性有效期有效
         */
        private Integer gap;
        /**
         * 优惠券模版的失效时间，两类规则都有效
         */
        private Long deadline;

        boolean validate() {
            return null != PeriodTypeEnum.of(period) && gap > 0 && deadline > 0;
        }
    }

    /**
     * 折扣，需要和优惠券类型配合决定
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Discount {
        /**
         * 额度：满减（20）元，折扣（85）折，立减（10）元
         */
        private Integer quota;
        /**
         * 基准 需要满多少才可用 只针对满减类型
         */
        private Integer base;

        boolean validate() {
            return quota > 0 && base > 0;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Usage {
        /**
         * 省份
         */
        private String province;
        /**
         * 城市
         */
        private String city;
        /**
         * 商品类型,list的json
         */
        private String goodsType;

        boolean validate() {
            return StringUtils.isNotBlank(province) && StringUtils.isNotBlank(city) && StringUtils.isNotBlank(goodsType);
        }
    }
}
