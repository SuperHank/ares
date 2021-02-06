package com.hank.ares.service.impl;

import com.hank.ares.model.dto.SingleAddReqDto;
import com.hank.ares.service.IRouteService;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RouteServiceImpl implements IRouteService {

    @Override
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
        // TODO  新增路由配置
    }
}
