package com.hank.ares.service.impl;

import com.hank.ares.model.dto.SingleAddReqDto;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    public void singleAdd(SingleAddReqDto reqDto) throws URISyntaxException {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(reqDto.getId());

        List<PredicateDefinition> predicates = new ArrayList<>();
        PredicateDefinition definition = new PredicateDefinition();
        //注意name
        definition.setName("Path");
        definition.addArg("pattern", "/api2/**");
        predicates.add(definition);
        routeDefinition.setPredicates(predicates);

        List<FilterDefinition> filters = new ArrayList<>();
        FilterDefinition filterDefinition = new FilterDefinition();
        //注意name
        filterDefinition.setName("StripPrefix");
        filterDefinition.addArg("parts", "1");
        filters.add(filterDefinition);
        routeDefinition.setFilters(filters);

        URI uri = new URI("lb://client-manage");
        routeDefinition.setUri(uri);
        routeDefinition.setOrder(0);
    }

    private void notifyChanged() {
        this.publisher.publishEvent(new RefreshRoutesResultEvent(this));
    }

    /**
     * 新增路由
     */
    public String add(RouteDefinition definition) {
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        notifyChanged();
        return "success";
    }

    /**
     * 更新路由
     */
    public String update(RouteDefinition definition) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception e) {
            return "update fail,not find route  routeId: " + definition.getId();
        }
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            notifyChanged();
            return "success";
        } catch (Exception e) {
            return "update route  fail";
        }


    }

    /**
     * 删除路由
     */
    public String delete(String id) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();

            notifyChanged();
            return "delete success";
        } catch (Exception e) {
            e.printStackTrace();
            return "delete fail";
        }

    }
}
