package com.hank.ares.settle;

import com.alibaba.fastjson.JSON;
import com.hank.ares.client.coupon.CuoponTemplateClient;
import com.hank.ares.model.CouponTemplateDto;
import com.hank.ares.model.settlement.GoodsDto;
import com.hank.ares.model.settlement.SettlementDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 规则执行器抽象类, 定义通用方法
 */
public abstract class AbstractExecutor {

    @Autowired
    protected CuoponTemplateClient cuoponTemplateClient;

    /**
     * 处理商品类型与优惠券限制不匹配的情况
     *
     * @param settlement {@link SettlementDto} 用户传递的结算信息
     * @param goodsSum   商品总价
     * @return {@link SettlementDto} 已经修改过的结算信息
     */
    protected SettlementDto processGoodsTypeNotSatisfy(SettlementDto settlement, double goodsSum) {

        // 当商品类型不满足时, 直接返回总价, 并清空优惠券
        if (!isGoodsTypeSatisfy(settlement)) {
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateIds(Collections.emptyList());
            return settlement;
        }

        return null;
    }

    /**
     * 校验商品类型与优惠券是否匹配
     * 需要注意:
     * 1. 这里实现的单品类优惠券的校验, 多品类优惠券重载此方法
     * 2. 商品只需要有一个优惠券要求的商品类型去匹配就可以
     */

    protected boolean isGoodsTypeSatisfy(SettlementDto settlement) {

        List<Integer> goodsType = settlement.getGoodsDtos().stream().map(GoodsDto::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();

        settlement.getCouponAndTemplateIds().forEach(ct -> {
            CouponTemplateDto couponTemplateDto = cuoponTemplateClient.getById(ct.getTemplateId());
            templateGoodsType.add(JSON.parseObject(couponTemplateDto.getRule().getUsage().getGoodsType(), Integer.class));
        });

        // 存在交集即可
        return CollectionUtils.isNotEmpty(CollectionUtils.intersection(goodsType, templateGoodsType));
    }

    /**
     * 商品总价
     */
    protected double goodsCostSum(List<GoodsDto> goodsDtos) {
        return goodsDtos.stream().mapToDouble(g -> g.getPrice() * g.getCount()).sum();
    }

    /**
     * 保留两位小数
     */
    protected double retain2Decimals(double value) {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 最小支付费用
     */
    protected double minCost() {
        return 0.1;
    }
}
