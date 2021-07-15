package com.hank.ares.executor;

import com.hank.ares.enums.permission.RuleFlagEnum;
import com.hank.ares.model.SettlementInfo;

/**
 * 优惠券模板规则处理器接口定义
 */
public interface RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return {@link RuleFlagEnum}
     */
    RuleFlagEnum ruleConfig();

    /**
     * 优惠券规则的计算
     *
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    SettlementInfo computeRule(SettlementInfo settlement);
}
