package com.hank.ares.biz.impl;

import com.google.common.base.Stopwatch;
import com.hank.ares.dao.CouponTemplateDao;
import com.hank.ares.enums.common.RedisPrefixEnum;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.biz.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步服务接口实现
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    @Autowired
    private CouponTemplateDao templateDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据模板异步的创建优惠券码
     *
     * @param template {@link CouponTemplate} 优惠券模板实体
     */
    @Async("getAsyncExecutor")
    @Override
    @Transactional
    public void asyncConstructCouponByTemplate(CouponTemplate template) {

        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(template);

        String redisKey = String.format("%s%s", RedisPrefixEnum.COUPON_TEMPLATE.getPrefix(), template.getId().toString());
        log.info("Push CouponCode To Redis: {}", redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));

        watch.stop();
        log.info("Construct CouponCode By Template Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));

        template.setAvailable(true);
        templateDao.save(template);
        log.info("CouponTemplate({}) Is Available!", template.getId());

        // TODO 异步发送短信或者邮件通知优惠券模板已经可用
    }

    /**
     * 构造优惠券码
     * 优惠券码(对应于每一张优惠券, 18位)
     * 前四位: 产品线 + 类型
     * 中间六位: 日期随机(190101)
     * 后八位: 0 ~ 9 随机数构成
     *
     * @param template {@link CouponTemplate} 实体类
     * @return Set<String> 与 template.count 相同个数的优惠券码
     */
    private Set<String> buildCouponCode(CouponTemplate template) {

        Stopwatch watch = Stopwatch.createStarted();

        Set<String> result = new HashSet<>(template.getCouponCount());

        // 前四位
        String prefix4 = template.getProductLineEnum().getCode().toString() + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());

        while (result.size() < template.getCouponCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        watch.stop();
        log.info("Build Coupon Code Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    /**
     * 构造优惠券码的后 14 位
     *
     * @param date 创建优惠券的日期
     * @return 14 位优惠券码
     */
    private String buildCouponCodeSuffix14(String date) {

        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};

        // 中间六位
        List<Character> chars = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream().map(Object::toString).collect(Collectors.joining());

        // 后八位
        String suffix8 = RandomStringUtils.random(1, bases) + RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;
    }
}
