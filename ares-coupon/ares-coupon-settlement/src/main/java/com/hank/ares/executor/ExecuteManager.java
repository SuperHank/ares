package com.hank.ares.executor;

import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.enums.coupon.CouponCategoryEnum;
import com.hank.ares.enums.permission.RuleFlagEnum;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.SettlementInfo;
import com.hank.ares.util.ExceptionThen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 优惠券结算规则执行管理器
 * 即根据用户的请求(SettlementInfo)找到对应的 Executor, 去做结算
 * BeanPostProcessor: Bean 后置处理器
 */
@Slf4j
@Component

public class ExecuteManager implements BeanPostProcessor {

    /**
     * 规则执行器映射
     */
    private static Map<RuleFlagEnum, RuleExecutor> executorIndex = new HashMap<>(RuleFlagEnum.values().length);

    /**
     * 优惠券结算规则计算入口
     */
    public SettlementInfo computeRule(SettlementInfo settlement) throws CouponException {
        // 优惠券模版类型
        List<CouponCategoryEnum> categories = settlement.getCouponAndTemplateInfos().stream()
                .map(SettlementInfo.CouponAndTemplateInfo::getTemplate)
                .map(i -> CouponCategoryEnum.of(i.getCategory())).collect(Collectors.toList());

        RuleFlagEnum ruleFlagEnum = RuleFlagEnum.of(categories);
        return executorIndex.get(ruleFlagEnum).computeRule(settlement);
    }

    /**
     * 在 bean 初始化之前去执行(before)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (!(bean instanceof RuleExecutor)) {
            return bean;
        }

        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlagEnum ruleFlagEnum = executor.ruleConfig();

        ExceptionThen.then(executorIndex.containsKey(ruleFlagEnum), ResultCode.SYSTEM_ERROR,
                String.format("There is already an executor for rule flag: %s", ruleFlagEnum));

        log.info("Load executor {} for rule flag {}.", executor.getClass(), ruleFlagEnum.getDescription());
        executorIndex.put(ruleFlagEnum, executor);

        return null;
    }

    /**
     * 在 bean 初始化之后去执行(after)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
