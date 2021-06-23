package com.hank.ares.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRuleBo {
    /**
     * 有效期限规则
     */
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
    }
}
