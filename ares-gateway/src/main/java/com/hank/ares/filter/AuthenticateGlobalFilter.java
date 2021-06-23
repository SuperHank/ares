package com.hank.ares.filter;

import com.hank.ares.enums.common.ReqParamEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局认证过滤器
 */
@Slf4j
@Component
public class AuthenticateGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String userId = exchange.getRequest().getQueryParams().getFirst(ReqParamEnum.ARES_USER_ID.getParamKey());
        String userToken = exchange.getRequest().getQueryParams().getFirst(ReqParamEnum.ARES_TOKEN.getParamKey());
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(userToken)) {
            log.error("userId or userToken is null. userId:{},userToken:{}", userId, userToken);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            String message = String.format("message:%s", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            DataBuffer buffer = response.bufferFactory().wrap(message.getBytes());
            return response.writeWith(Mono.just(buffer));
        }

        String tokenInCache = redisTemplate.opsForValue().get(userId);

        if (StringUtils.isBlank(tokenInCache)) {
            log.error("token in cache is null...");
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            String message = String.format("message:%s", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            DataBuffer buffer = response.bufferFactory().wrap(message.getBytes());
            return response.writeWith(Mono.just(buffer));
        }

        if (!StringUtils.equals(tokenInCache, userToken)) {
            log.error("token in wrong...");
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            String message = String.format("message:%s", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            DataBuffer buffer = response.bufferFactory().wrap(message.getBytes());
            return response.writeWith(Mono.just(buffer));
        }

        log.info("token is ok!");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
