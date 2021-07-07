package com.hank.ares.controller;

import com.alibaba.fastjson.JSON;
import com.hank.ares.exception.CouponException;
import com.hank.ares.executor.ExecuteManager;
import com.hank.ares.model.SettlementInfo;
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
public class SettlementController {

    /**
     * 结算规则执行管理器
     */
    private final ExecuteManager executeManager;

    @Autowired
    public SettlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * 优惠券结算
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement) throws CouponException {
        log.info("settlement: {}", JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
}
