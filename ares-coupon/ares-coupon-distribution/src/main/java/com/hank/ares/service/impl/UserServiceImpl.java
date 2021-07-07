package com.hank.ares.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hank.ares.constant.CouponConstant;
import com.hank.ares.enums.CouponStatus;
import com.hank.ares.exception.CouponException;
import com.hank.ares.feigh.SettlementClient;
import com.hank.ares.feigh.TemplateClient;
import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.CouponTemplateSDK;
import com.hank.ares.model.GoodsInfo;
import com.hank.ares.model.SettlementInfo;
import com.hank.ares.model.dto.req.AcquireTemplateReqDto;
import com.hank.ares.model.vo.CouponClassify;
import com.hank.ares.model.vo.CouponKafkaMessage;
import com.hank.ares.service.IRedisService;
import com.hank.ares.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户服务相关的接口实现
 * 所有的操作过程, 状态都保存在 Redis 中, 并通过 Kafka 把消息传递到 MySQL 中
 * 为什么使用 Kafka, 而不是直接使用 SpringBoot 中的异步处理 ?
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private CouponMapper couponMapper;

    @Qualifier("redisServiceImpl")
    @Autowired
    private IRedisService redisService;

    /**
     * 模板微服务客户端
     */
    @Autowired
    private TemplateClient templateClient;

    /**
     * 结算微服务客户端
     */
    @Autowired
    private SettlementClient settlementClient;

    /**
     * Kafka 客户端
     */
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 根据用户 id 和状态查询优惠券记录
     *
     * @param userId 用户 id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     * @throws CouponException
     */
    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {
        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;
        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("coupon cache is not empty:{},{}", userId, status);
            preTarget = curCached;
        } else {
            log.debug("coupon cache is empty, get coupon from db:{},{}", userId, status);
            QueryWrapper<Coupon> wrapper = new QueryWrapper<Coupon>().eq("user_id", userId).eq("status", status);
            List<Coupon> dbCoupons = couponMapper.selectList(wrapper);
            // 如果数据库中没有记录, 直接返回就可以, Cache 中已经加入了一张无效的优惠券
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not have coupon:{},{}", userId, status);
                return Collections.emptyList();
            }

            // 填充 dbCoupons的 templateSDK 字段
            Map<Integer, CouponTemplateSDK> id2TemplateSDK = templateClient.findIds2TemplateSDK(
                    dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toList())).getData();
            dbCoupons.forEach(dc -> dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId())));
            // 数据库中存在记录
            preTarget = dbCoupons;
            // 将记录写入cache
            redisService.addCouponToCache(userId, preTarget, status);
        }
        // 将无效优惠券剔除
        preTarget = preTarget.stream().filter(c -> c.getId() != -1).collect(Collectors.toList());
        // 如果当前获取的是可用优惠券, 还需要做对已过期优惠券的延迟处理
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            // 如果已过期状态不为空, 需要做延迟处理
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupons To Cache From FindCouponsByStatus: " +
                        "{}, {}", userId, status);
                redisService.addCouponToCache(
                        userId, classify.getExpired(),
                        CouponStatus.EXPIRED.getStatus()
                );
                // 发送到 kafka 中做异步处理
                CouponKafkaMessage couponKafkaMessage = new CouponKafkaMessage(CouponStatus.EXPIRED.getStatus(),
                        classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList()));
                kafkaTemplate.send(CouponConstant.TOPIC, JSON.toJSONString(couponKafkaMessage));
            }

            return classify.getUsable();
        }
        return preTarget;
    }

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     *
     * @param userId 用户 id
     * @return {@link CouponTemplateSDK}s
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getData();
        log.debug("Find All Template(From TemplateClient) Count:{}", templateSDKS.size());

        templateSDKS = templateSDKS.stream().filter(i -> i.getRule().getExpiration().getDeadline() > curTime).collect(Collectors.toList());

        log.info("Find Usable Template Count: {}", templateSDKS.size());

        HashMap<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(i -> limit2Template.put(i.getId(), Pair.of(i.getRule().getLimitation(), i)));

        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponsByStatus(userId, CouponStatus.USABLE.getStatus());

        log.debug("Current User Has Usable Coupons: {}, {}", userId, userUsableCoupons.size());


        // key 是 TemplateId
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));

        // 根据 Template 的 Rule 判断是否可以领取优惠券模板
        limit2Template.forEach((k, v) -> {
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();

            if (templateId2Coupons.containsKey(k) && templateId2Coupons.get(k).size() >= limitation) {
                return;
            }
            result.add(templateSDK);
        });

        return result;
    }

    /**
     * 用户领取优惠券
     * 1. 从 TemplateClient 拿到对应的优惠券, 并检查是否过期
     * 2. 根据 limitation 判断用户是否可以领取
     * 3. save to db
     * 4. 填充 CouponTemplateSDK
     * 5. save to cache
     *
     * @param request {@link AcquireTemplateReqDto}
     * @return {@link Coupon}
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateReqDto request) throws CouponException {
        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(Collections.singletonList(request.getTemplateSDK().getId())).getData();
        // 优惠券模板是需要存在的
        if (id2Template.size() <= 0) {
            log.error("Can Not Acquire Template From TemplateClient:{}", request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Template From TemplateClient");
        }
        // 用户是否可以领取这张优惠券
        List<Coupon> userUsableCoupons = findCouponsByStatus(request.getUserId(), CouponStatus.USABLE.getStatus());
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));

        if (templateId2Coupons.containsKey(request.getTemplateSDK().getId())
                && templateId2Coupons.get(request.getTemplateSDK().getId()).size() >= request.getTemplateSDK().getRule().getLimitation()) {
            log.error("Exceed Template Assign Limitation: {}", request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }

        // 尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(request.getTemplateSDK().getId());
        if (StringUtils.isBlank(couponCode)) {
            log.error("Can Not Acquire Coupon Code:{}", request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Coupon Code");
        }

        // 填充 Coupon 对象的 CouponTemplateSDK, 一定要在放入缓存之前去填充
        Coupon newCoupon = new Coupon(request.getTemplateSDK().getId(), request.getUserId(), couponCode, CouponStatus.USABLE);
        couponMapper.insert(newCoupon);
        newCoupon.setTemplateSDK(request.getTemplateSDK());

        // 放入缓存中
        redisService.addCouponToCache(request.getUserId(), Collections.singletonList(newCoupon), CouponStatus.USABLE.getStatus());

        return newCoupon;
    }

    /**
     * 结算(核销)优惠券
     * 这里需要注意, 规则相关处理需要由 Settlement 系统去做, 当前系统仅仅做
     * 业务处理过程(校验过程)
     *
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {

        // 当没有传递优惠券时, 直接返回商品总价
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = info.getCouponAndTemplateInfos();
        if (CollectionUtils.isEmpty(ctInfos)) {

            log.info("Empty Coupons For Settle.");

            double goodsSum = 0.0;

            for (GoodsInfo gi : info.getGoodsInfos()) {
                goodsSum += gi.getPrice() + gi.getCount();
            }

            // 没有优惠券也就不存在优惠券的核销, SettlementInfo 其他的字段不需要修改
            info.setCost(retain2Decimals(goodsSum));
        }

        // 校验传递的优惠券是否是用户自己的
        List<Coupon> coupons = findCouponsByStatus(info.getUserId(), CouponStatus.USABLE.getStatus());
        Map<Integer, Coupon> id2Coupon = coupons.stream().collect(Collectors.toMap(Coupon::getId, Function.identity()));
        if (MapUtils.isEmpty(id2Coupon) || !CollectionUtils.isSubCollection(ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList()), id2Coupon.keySet())) {
            log.info("{}", id2Coupon.keySet());
            log.info("{}", ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList()));
            log.error("User Coupon Has Some Problem, It Is Not SubCollection Of Coupons!");
            throw new CouponException("User Coupon Has Some Problem, It Is Not SubCollection Of Coupons!");
        }

        log.debug("Current Settlement Coupons Is User's: {}", ctInfos.size());

        List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
        ctInfos.forEach(ci -> settleCoupons.add(id2Coupon.get(ci.getId())));

        // 通过结算服务获取结算信息
        SettlementInfo processedInfo = settlementClient.computeRule(info).getData();
        if (processedInfo.getEmploy() && CollectionUtils.isNotEmpty(processedInfo.getCouponAndTemplateInfos())) {
            log.info("Settle User Coupon: {}, {}", info.getUserId(), JSON.toJSONString(settleCoupons));
            // 更新缓存
            redisService.addCouponToCache(info.getUserId(), settleCoupons, CouponStatus.USED.getStatus());
            // 更新 db
            String msg = JSON.toJSONString(new CouponKafkaMessage(CouponStatus.USED.getStatus(), settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList())));
            kafkaTemplate.send(CouponConstant.TOPIC, msg);
        }

        return processedInfo;
    }


    /**
     * 保留两位小数
     */
    private double retain2Decimals(double value) {
        // BigDecimal.ROUND_HALF_UP 代表四舍五入
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
