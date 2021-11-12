package com.hank.ares.settle.executor;

import com.hank.ares.enums.permission.RuleFlagEnum;
import com.hank.ares.model.CouponTemplateDto;
import com.hank.ares.model.settlement.SettlementDto;
import com.hank.ares.settle.AbstractExecutor;
import com.hank.ares.settle.RuleExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 满减优惠券结算规则执行器
 */
@Slf4j
@Component
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return {@link RuleFlagEnum}
     */
    @Override
    public RuleFlagEnum ruleConfig() {
        return RuleFlagEnum.MANJIAN;
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
            log.debug("ManJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 判断满减是否符合折扣标准
        CouponTemplateDto templateSDK = cuoponTemplateClient.getTemplateById(settlement.getCouponAndTemplateIds().get(0).getTemplateId());
        double base = (double) templateSDK.getRule().getDiscount().getBase();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // 如果不符合标准, 则直接返回商品总价
        if (goodsSum < base) {
            log.debug("Current Goods Cost Sum < ManJian Coupon Base!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateIds(Collections.emptyList());
            return settlement;
        }

        // 计算使用优惠券之后的价格 - 结算
        settlement.setCost(retain2Decimals(Math.max((goodsSum - quota), minCost())));
        log.debug("Use ManJian Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());

        return settlement;
    }
}
