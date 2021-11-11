package com.hank.ares.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hank.ares.client.coupon.CouponSettlementClient;
import com.hank.ares.client.coupon.CuoponTemplateClient;
import com.hank.ares.constant.KafkaTopicConstants;
import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.enums.coupon.CouponStatusEnum;
import com.hank.ares.exception.CouponException;
import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.CouponTemplateDto;
import com.hank.ares.model.settlement.GoodsDto;
import com.hank.ares.model.settlement.SettlementDto;
import com.hank.ares.model.dto.req.AcquireTemplateReqDto;
import com.hank.ares.model.kafka.CouponKafkaMsg;
import com.hank.ares.model.vo.CouponClassify;
import com.hank.ares.biz.service.IRedisService;
import com.hank.ares.biz.service.IUserService;
import com.hank.ares.util.AmtUtil;
import com.hank.ares.util.ExceptionThen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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
    private CuoponTemplateClient templateClient;

    @Autowired
    private CouponSettlementClient settlementClient;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 根据用户 id 和状态查询优惠券记录
     *
     * @param userId 用户 id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     */
    @Override
    public List<Coupon> findCouponsByStatus(Integer userId, Integer status) throws CouponException {
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
            Map<Integer, CouponTemplateDto> id2TemplateSDK = templateClient.getByIds(dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toList()));
            dbCoupons.forEach(dc -> {
                dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId()));
            });
            preTarget = dbCoupons;
            redisService.addCouponToCache(userId, preTarget, status);
        }
        // 将无效的优惠券剔除
        preTarget = preTarget.stream().filter(i -> i.getId() != -1).collect(Collectors.toList());
        preTarget.forEach(i -> {
            CouponTemplateDto templateSDK = templateClient.getById(i.getTemplateId());
            i.setTemplateSDK(templateSDK);
        });
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

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     *
     * @param userId 用户 id
     * @return {@link CouponTemplateDto}s
     */
    @Override
    public List<CouponTemplateDto> findAvailableTemplate(Integer userId) throws CouponException {
        long curTime = new Date().getTime();
        List<CouponTemplateDto> templateSDKS = templateClient.getAllUsableTemplate();
        log.debug("Find All Template From Template Client Count :{}", templateSDKS.size());

        // 过滤过期的优惠券模版
        templateSDKS = templateSDKS.stream().filter(t -> t.getRule().getExpiration().getDeadline() > curTime).collect(Collectors.toList());

        log.info("Find Usable Template Count :{}", templateSDKS.size());

        // key 是 TemplateID
        // value 中的 left 是 Template limitation， right 是 优惠券模版
        HashMap<Integer, Pair<Integer, CouponTemplateDto>> limit2Template = new HashMap<>(templateSDKS.size());

        templateSDKS.forEach(t -> {
            limit2Template.put(t.getId(), Pair.of(t.getRule().getLimitation(), t));
        });

        List<CouponTemplateDto> result = new ArrayList<>(limit2Template.size());

        List<Coupon> userUsableCoupons = findCouponsByStatus(userId, CouponStatusEnum.USED.getStatus());

        log.debug("Current User Has Usable Coupons:{},{}", userId, userUsableCoupons.size());

        // key 是 TemplateId
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));

        // 根据 Template 的 Rule 判断是否可以领取优惠券模版
        limit2Template.forEach((k, v) -> {
            int limitation = v.getLeft();
            CouponTemplateDto templateSDK = v.getRight();

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
        CouponTemplateDto templateSDK = templateClient.getById(request.getTemplateSDKId());

        ExceptionThen.then(templateSDK == null, ResultCode.PARAM_IS_INVALID, "Can Not Acquire Template From TemplateClient ;{}" + request.getTemplateSDKId());
        ExceptionThen.then(templateSDK.getRule().getExpiration().getDeadline() < new Date().getTime(), ResultCode.PARAM_IS_INVALID, "Template Has Expired");

        List<Coupon> userUsableCoupons = findCouponsByStatus(request.getUserId(), CouponStatusEnum.USABLE.getStatus());

        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));

        if (templateId2Coupons.containsKey(templateSDK.getId()) && templateId2Coupons.get(templateSDK.getId()).size() >= templateSDK.getRule().getLimitation()) {
            log.info("Exceed Template Assign Limitation:{}", templateSDK.getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }

        // 尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(templateSDK.getId());
        ExceptionThen.then(StringUtils.isBlank(couponCode), ResultCode.SYSTEM_ERROR, "Can Not Acquire Coupon Code");

        Coupon newCoupon = new Coupon(templateSDK.getId(), request.getUserId(), couponCode, CouponStatusEnum.USABLE);
        couponDao.insert(newCoupon);

        // 填充 Coupon 对象的 CouponTemplateSDK 一定要在放入缓存中之前去填充
        newCoupon.setTemplateSDK(templateSDK);

        // 放入缓存中
        redisService.addCouponToCache(request.getUserId(), Collections.singletonList(newCoupon), CouponStatusEnum.USABLE.getStatus());

        return newCoupon;
    }

    /**
     * 结算(核销)优惠券
     * 这里需要注意, 规则相关处理需要由 Settlement 系统去做, 当前系统仅仅做
     * 业务处理过程(校验过程)
     *
     * @param info {@link SettlementDto}
     * @return {@link SettlementDto}
     */
    @Override
    public SettlementDto settlement(SettlementDto info) throws CouponException {
        // 当没有传递优惠券时，直接返回商品总价
        if (CollectionUtils.isEmpty(info.getCouponAndTemplateIds())) {
            log.info("Empty Coupons For Settle");
            double goodsSum = 0.0;
            for (GoodsDto goodsDto : info.getGoodsDtos()) {
                goodsSum += goodsDto.getPrice() * goodsDto.getCount();
            }

            info.setCost(AmtUtil.retain2Decimals(goodsSum));
        }

        // 校验传递的优惠券是不是用户自己的
        List<Coupon> userUsableCoupons = findCouponsByStatus(info.getUserId(), CouponStatusEnum.USABLE.getStatus());
        Map<Integer, Coupon> id2Coupon = userUsableCoupons.stream().collect(Collectors.toMap(Coupon::getId, Function.identity()));

        if (MapUtils.isEmpty(id2Coupon) || !CollectionUtils.isSubCollection(info.getCouponAndTemplateIds().stream().map(SettlementDto.CouponAndTemplateId::getId).collect(Collectors.toList()), id2Coupon.keySet())) {
            log.info("{}", id2Coupon.keySet());
            log.error("User Coupon Has Some Problem, It Is Not SubCollection Of Coupons");
            throw new CouponException("User Coupon Has Some Problem, It Is Not SubCollection Of Coupons");
        }

        log.debug("Current Settlement Coupons Is User's :{}", info.getCouponAndTemplateIds().size());

        List<Coupon> settleCoupons = new ArrayList<>(info.getCouponAndTemplateIds().size());
        info.getCouponAndTemplateIds().forEach(ci -> settleCoupons.add(id2Coupon.get(ci.getId())));

        // 通过结算服务获取结算信息
        SettlementDto processedInfo = settlementClient.computeRule(info);
        if (processedInfo.getEmploy() && CollectionUtils.isNotEmpty(processedInfo.getCouponAndTemplateIds())) {
            log.info("Settle User Coupon: {}, {}", info.getUserId(), JSON.toJSONString(settleCoupons));
            // 更新缓存
            redisService.addCouponToCache(info.getUserId(), settleCoupons, CouponStatusEnum.USED.getStatus());
            // 延迟更新 db
            kafkaTemplate.send(KafkaTopicConstants.TOPIC,
                    JSON.toJSONString(new CouponKafkaMsg(CouponStatusEnum.USED.getStatus(), settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList())))
            );
        }

        return processedInfo;
    }
}
