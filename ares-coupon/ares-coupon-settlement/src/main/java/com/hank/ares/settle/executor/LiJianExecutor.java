package com.hank.ares.settle.executor;

import com.hank.ares.enums.biz.permission.RuleFlagEnum;
import com.hank.ares.model.CouponTemplateDto;
import com.hank.ares.model.settlement.SettlementDto;
import com.hank.ares.settle.AbstractExecutor;
import com.hank.ares.settle.RuleExecutor;
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
     * @param settlement {@link SettlementDto} 包含了选择的优惠券
     * @return {@link SettlementDto} 修正过的结算信息
     */
    @Override
    public SettlementDto computeRule(SettlementDto settlement) {

        double goodsSum = retain2Decimals(goodsCostSum(settlement.getGoodsDtos()));
        SettlementDto probability = processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (probability != null) {
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 立减优惠券直接使用, 没有门槛
        CouponTemplateDto templateSDK = cuoponTemplateClient.getTemplateById(settlement.getCouponAndTemplateIds().get(0).getTemplateId());
        // 抵扣额度
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // 计算使用优惠券之后的价格 - 结算
        settlement.setCost(Math.max(retain2Decimals(goodsSum - quota), minCost()));

        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());

        return settlement;
    }
}
