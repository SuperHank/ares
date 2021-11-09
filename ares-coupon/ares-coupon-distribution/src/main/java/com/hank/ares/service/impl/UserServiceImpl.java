package com.hank.ares.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hank.ares.constant.KafkaTopicConstants;
import com.hank.ares.enums.coupon.CouponStatusEnum;
import com.hank.ares.exception.CouponException;
import com.hank.ares.feign.SettlementServiceFeignClient;
import com.hank.ares.feign.TemplateServiceFeignClient;
import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.CouponTemplateSDK;
import com.hank.ares.model.SettlementInfo;
import com.hank.ares.model.dto.req.AcquireTemplateReqDto;
import com.hank.ares.model.kafka.CouponKafkaMsg;
import com.hank.ares.model.vo.CouponClassify;
import com.hank.ares.service.IRedisService;
import com.hank.ares.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务相关的接口实现
 * 所有的操作过程, 状态都保存在 Redis 中, 并通过 Kafka 把消息传递到 MySQL 中
 * 为什么使用 Kafka, 而不是直接使用 SpringBoot 中的异步处理 ?
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private CouponMapper couponDao;

    @Autowired
    private IRedisService redisService;

    @Autowired
    private TemplateServiceFeignClient templateClient;

    @Autowired
    private SettlementServiceFeignClient settlementClient;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {
        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;

        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("coupon cache is not empty :{},{}", userId, status);
            preTarget = curCached;
        } else {
            log.debug("coupon cache is empty, get coupon from db :{},{}", userId, status);
            QueryWrapper<Coupon> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            wrapper.eq("status", status);

            List<Coupon> dbCoupons = couponDao.selectList(wrapper);
            // 如果数据库中没有记录，直接返回就可以，Cache中已经加入了一张无效优惠券
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not hava coupon :{},{}", userId, status);
                return dbCoupons;
            }

            // 填充 dbCoupons 的 templateSDK 字段
            Map<Integer, CouponTemplateSDK> id2TemplateSDK = templateClient.getByIds(dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toList())).getData();
            dbCoupons.forEach(dc -> {
                dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId()));
            });
            preTarget = dbCoupons;
            redisService.addCouponToCache(userId, preTarget, status);
        }
        // 将无效的优惠券剔除
        preTarget = preTarget.stream().filter(i -> i.getId() != -1).collect(Collectors.toList());
        // 如果当前获取的是可用优惠券，还需要对已过期优惠券做延迟处理
        if (CouponStatusEnum.of(status) == CouponStatusEnum.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expire Coupons To Cache From FindCouponByStatus:{},{}", userId, status);
                redisService.addCouponToCache(userId, classify.getExpired(), CouponStatusEnum.EXPIRED.getStatus());
                // 发送到 kafka 中做异步处理
                kafkaTemplate.send(KafkaTopicConstants.TOPIC, JSON.toJSONString(new CouponKafkaMsg(CouponStatusEnum.EXPIRED.getStatus(), classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList()))));
            }
            return classify.getUsable();
        }

        return preTarget;
    }

    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        return null;
    }

    @Override
    public Coupon acquireTemplate(AcquireTemplateReqDto request) throws CouponException {
        return null;
    }

    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        return null;
    }
}
