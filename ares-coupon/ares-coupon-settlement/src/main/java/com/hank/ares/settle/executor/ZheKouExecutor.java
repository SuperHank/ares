package com.hank.ares.settle.executor;

import com.hank.ares.enums.permission.RuleFlagEnum;
import com.hank.ares.model.CouponTemplateDto;
import com.hank.ares.model.settlement.SettlementDto;
import com.hank.ares.settle.AbstractExecutor;
import com.hank.ares.settle.RuleExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 折扣优惠券结算规则执行器
 */
@Slf4j
@Component
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return {@link RuleFlagEnum}
     */
    @Override
    public RuleFlagEnum ruleConfig() {
        return RuleFlagEnum.ZHEKOU;
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
        if (null != probability) {
            log.debug("ZheKou Template Is Not Match GoodsType!");
            return probability;
        }

        // 折扣优惠券可以直接使用, 没有门槛
        CouponTemplateDto couponTemplateDto = cuoponTemplateClient.getTemplateById(settlement.getCouponAndTemplateIds().get(0).getTemplateId());
        double quota = (double) couponTemplateDto.getRule().getDiscount().getQuota();

        // 计算使用优惠券之后的价格
        settlement.setCost(Math.max(retain2Decimals((goodsSum * (quota / 100))), minCost()));
        log.debug("Use ZheKou Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());

        return settlement;
    }
}
