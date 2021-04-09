package com.hank.ares.config;


import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class GatewayConfiguration {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @PostConstruct
    public void doInit() {
        // 先加载自定义Api分组
        initCustomizedApis();
        // 再加载路由规则
        initGatewayRules();
    }

    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        ApiDefinition api1 = new ApiDefinition("api-ares-order")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/order/order/hello")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        ApiDefinition api2 = new ApiDefinition("api-ares-settlement")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/settlement/settlement/hello")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        definitions.add(api1);
        definitions.add(api2);
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        /**
         * resource：资源名称，可以是网关中route名称或者用户自定义的API分组名称
         * count：限流阈值
         * intervalSec：统计时间窗口，单位是秒 默认是1秒
         */

        //  网关route名称
        rules.add(new GatewayFlowRule("ares-order")
                .setCount(3) // 限流阈值
                .setIntervalSec(10) // 统计时间窗口，单位是秒 默认是1秒
        );
        rules.add(new GatewayFlowRule("ares-settlement")
                .setCount(5) // 限流阈值
                .setIntervalSec(10) // 统计时间窗口，单位是秒 默认是1秒
        );

        // 自定义API分组名称
        rules.add(new GatewayFlowRule("api-ares-order")
                .setCount(3) // 限流阈值
                .setIntervalSec(10) // 统计时间窗口，单位是秒 默认是1秒
        );
        rules.add(new GatewayFlowRule("api-ares-settlement")
                .setCount(5) // 限流阈值
                .setIntervalSec(10) // 统计时间窗口，单位是秒 默认是1秒
        );
        GatewayRuleManager.loadRules(rules);
    }
}
