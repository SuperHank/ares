package com.hank.ares.api.controller;

import com.alibaba.fastjson.JSON;
import com.hank.ares.settle.ExecuteManager;
import com.hank.ares.model.settlement.SettlementDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结算服务 Controller
 */
@Slf4j
@RestController
public class SettlementApiController {

    /**
     * 结算规则执行管理器
     */
    @Autowired
    private ExecuteManager executeManager;

    /**
     * 优惠券结算
     */
    @PostMapping("/settlement/compute")
    public SettlementDto computeRule(@RequestBody SettlementDto settlement) {
        log.info("settlement: {}", JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
}
