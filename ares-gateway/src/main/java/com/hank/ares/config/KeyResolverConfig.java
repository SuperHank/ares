package com.hank.ares.config;

import com.hank.ares.enums.common.ReqParamEnum;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfig {

    /**
     * URI限流
     *
     * @return
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getURI().getPath());
    }

    /**
     * 参数限流
     *
     * @return
     */
    @Bean
    @Primary
    public KeyResolver parameterKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst(ReqParamEnum.ARES_USER_ID.getParamKey()));
    }
}
