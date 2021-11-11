package com.hank.ares.settle.executor;

import com.alibaba.fastjson.JSON;
import com.hank.ares.enums.coupon.CouponCategoryEnum;
import com.hank.ares.enums.permission.RuleFlagEnum;
import com.hank.ares.model.CouponTemplateDto;
import com.hank.ares.model.settlement.GoodsDto;
import com.hank.ares.model.settlement.SettlementDto;
import com.hank.ares.settle.AbstractExecutor;
import com.hank.ares.settle.RuleExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 满减 + 折扣优惠券结算规则执行器
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return {@link RuleFlagEnum}
     */
    @Override
    public RuleFlagEnum ruleConfig() {
        return RuleFlagEnum.MANJIAN_ZHEKOU;
    }

    /**
     * 校验商品类型与优惠券是否匹配
     * 需要注意:
     * 1. 这里实现的满减 + 折扣优惠券的校验
     * 2. 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
     *
     * @param settlement {@link SettlementDto} 用户传递的计算信息
     */
    @Override

    protected boolean isGoodsTypeSatisfy(SettlementDto settlement) {

        log.debug("Check ManJian And ZheKou Is Match Or Not!");
        List<Integer> goodsType = settlement.getGoodsDtos().stream().map(GoodsDto::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();

        settlement.getCouponAndTemplateIds().forEach(ct -> {
            CouponTemplateDto couponTemplateDto = cuoponTemplateClient.getById(ct.getTemplateId());
            templateGoodsType.addAll(JSON.parseObject(couponTemplateDto.getRule().getUsage().getGoodsType(), List.class));
        });

        // 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
        return CollectionUtils.isEmpty(CollectionUtils.subtract(goodsType, templateGoodsType));
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
        // 商品类型的校验
        SettlementDto probability = processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (null != probability) {
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return probability;
        }

        CouponTemplateDto manJian = null;
        CouponTemplateDto zheKou = null;

        for (SettlementDto.CouponAndTemplateId ct : settlement.getCouponAndTemplateIds()) {
            CouponTemplateDto templateSDK = cuoponTemplateClient.getById(ct.getTemplateId());
            if (CouponCategoryEnum.of(templateSDK.getCategory()) == CouponCategoryEnum.MANJIAN) {
                manJian = templateSDK;
            } else {
                zheKou = templateSDK;
            }
        }

        assert null != manJian;
        assert null != zheKou;

        // 当前的折扣优惠券和满减券如果不能共用(一起使用), 清空优惠券, 返回商品原价
        if (!isTemplateCanShared(manJian, zheKou)) {
            log.debug("Current ManJian And ZheKou Can Not Shared!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateIds(Collections.emptyList());
            return settlement;
        }

        List<CouponTemplateDto> ctInfos = new ArrayList<>();
        double manJianBase = (double) manJian.getRule().getDiscount().getBase();
        double manJianQuota = (double) manJian.getRule().getDiscount().getQuota();

        // 最终的价格
        double targetSum = goodsSum;

        // 先计算满减
        if (targetSum >= manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        // 再计算折扣
        double zheKouQuota = (double) zheKou.getRule().getDiscount().getQuota();
        targetSum *= zheKouQuota / 100;
        ctInfos.add(zheKou);

//        settlement.setCouponAndTemplateIds(ctInfos.stream().map(CouponTemplateSDK::getId).collect(Collectors.toList()));
        settlement.setCost(retain2Decimals(Math.max(targetSum, minCost())));

        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());

        return settlement;
    }

    /**
     * 当前的两张优惠券是否可以共用
     * 即校验 TemplateRule 中的 weight 是否满足条件
     */

    private boolean
    isTemplateCanShared(CouponTemplateDto manJian, CouponTemplateDto zheKou) {

        String manjianKey = manJian.getKey() + String.format("%04d", manJian.getId());
        String zhekouKey = zheKou.getKey() + String.format("%04d", zheKou.getId());

        List<String> allSharedKeysForManjian = new ArrayList<>();
        allSharedKeysForManjian.add(manjianKey);
        allSharedKeysForManjian.addAll(JSON.parseObject(manJian.getRule().getWeight(), List.class));

        List<String> allSharedKeysForZhekou = new ArrayList<>();
        allSharedKeysForZhekou.add(zhekouKey);
        allSharedKeysForZhekou.addAll(JSON.parseObject(zheKou.getRule().getWeight(), List.class));

        return CollectionUtils.isSubCollection(Arrays.asList(manjianKey, zhekouKey), allSharedKeysForManjian)
                || CollectionUtils.isSubCollection(Arrays.asList(manjianKey, zhekouKey), allSharedKeysForZhekou);
    }
}
