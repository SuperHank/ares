package com.hank.ares.executor.impl;

import com.hank.ares.enums.permission.RuleFlagEnum;
import com.hank.ares.executor.AbstractExecutor;
import com.hank.ares.executor.RuleExecutor;
import com.hank.ares.model.CouponTemplateSDK;
import com.hank.ares.model.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 立减优惠券结算规则执行器
 */
@Slf4j
@Component
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return {@link RuleFlagEnum}
     */
    @Override
    public RuleFlagEnum ruleConfig() {
        return RuleFlagEnum.LIJIAN;
    }

    /**
     * 优惠券规则的计算
     *
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum = retain2Decimals(goodsCostSum(settlement.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (probability != null) {
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 立减优惠券直接使用, 没有门槛
        CouponTemplateSDK templateSDK = cuoponTemplateClient.getById(settlement.getCouponAndTemplateIds().get(0).getTemplateId());
        // 抵扣额度
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // 计算使用优惠券之后的价格 - 结算
        settlement.setCost(Math.max(retain2Decimals(goodsSum - quota), minCost()));

        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());

        return settlement;
    }
}
