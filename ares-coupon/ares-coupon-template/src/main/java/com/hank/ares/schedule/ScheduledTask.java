package com.hank.ares.schedule;

import com.hank.ares.dao.CouponTemplateDao;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时清理已过期的优惠券模版
 */
@Slf4j
@Component
public class ScheduledTask {

    @Autowired
    private CouponTemplateDao templateDao;

    /**
     * 下线已过期的优惠券模版
     * 执行频率 1小时执行一次
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate() {
        log.info("Start To Expire CouponTemplate");
        List<CouponTemplate> templates = templateDao.findAllByExpired(true);
        if (CollectionUtils.isEmpty(templates)) {
            log.info("Done To Expire CouponTemplate.");
            return;
        }

        Date cur = new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());

        templates.forEach(t -> {
            // 根据优惠券模版中的 "过期规则" 校验模版是否过期
            TemplateRule rule = t.getRule();
            if (rule.getExpiration().getDeadline() < cur.getTime()) {
                t.setExpired(true);
                expiredTemplates.add(t);
            }
        });

        if (CollectionUtils.isNotEmpty(expiredTemplates)) {
            log.info("Expired CouponTemplate Num;{}", templateDao.saveAll(expiredTemplates));
        }
        log.info("Done To Expire CouponTemplate.");
    }
}
