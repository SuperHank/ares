package com.hank.ares.biz.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Functions;
import com.hank.ares.enums.common.RedisPrefixEnum;
import com.hank.ares.enums.coupon.CouponStatusEnum;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.Coupon;
import com.hank.ares.biz.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<Coupon> getCachedCoupons(String memberCode, Integer status) {
        log.info("Get Coupons From Cache:{},{}", memberCode, status);
        String key = status2RedisKey(memberCode, status);

        List<String> couponStrs = redisTemplate.opsForHash().values(key)
                .stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(couponStrs)) {
            saveEmptyCouponListToCache(memberCode, Collections.singletonList(status));
            return Collections.emptyList();
        }

        return couponStrs.stream().map(cs -> JSON.parseObject(cs, Coupon.class)).collect(Collectors.toList());
    }

    @Override
    public void saveEmptyCouponListToCache(String memberCode, List<Integer> status) {
        log.info("Save Empty List To Cache For User:{},Status:{}", memberCode, JSON.toJSONString(Coupon.invalidCoupon()));

        HashMap<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    redisOperations.opsForHash().putAll(status2RedisKey(memberCode, s), invalidCouponMap);
                });
                return null;
            }
        };

        log.info("Pipeline Exe Result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s", RedisPrefixEnum.COUPON_TEMPLATE.getPrefix(), templateId);
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);

        log.info("Acquire Coupon Code:{},{},{}", templateId, redisKey, couponCode);
        return couponCode;
    }

    @Override
    public void addCouponToCache(String memberCode, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache:{},{},{}", memberCode, JSON.toJSONString(coupons), status);

        CouponStatusEnum couponStatusEnum = CouponStatusEnum.of(status);
        switch (couponStatusEnum) {
            case USABLE:
                addCouponToCacheForUsable(memberCode, coupons);
                break;
            case USED:
                addCouponToCacheForUsed(memberCode, coupons);
                break;
            case EXPIRED:
                addCouponToCacheForExpired(memberCode, coupons);
                break;
        }
    }

    /**
     * 根据 status 获取到对应的 Redis Key
     */
    private String status2RedisKey(String memberCode, Integer status) {
        return String.format("%s_%s", CouponStatusEnum.of(status), memberCode);
    }

    /**
     * 新增加优惠券到 Cache 中
     */
    private void addCouponToCacheForUsable(String memberCode, List<Coupon> coupons) {

        // 如果 status 是 USABLE, 代表是新增加的优惠券
        // 只会影响一个 Cache: USER_COUPON_USABLE(新增)
        log.debug("Add Coupon To Cache For Usable.");

        Map<String, String> needCachedObject = new HashMap<>();
        coupons.forEach(coupon -> needCachedObject.put(String.valueOf(coupon.getId()), JSON.toJSONString(coupon)));

        String redisKey = status2RedisKey(memberCode, CouponStatusEnum.USABLE.getStatus());
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} Coupons To Cache: {}, {}", needCachedObject.size(), memberCode, redisKey);

        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

        needCachedObject.size();
    }

    /**
     * 将已使用的优惠券加入到 Cache 中
     */

    private void addCouponToCacheForUsed(String memberCode, List<Coupon> coupons) throws CouponException {

        // 如果 status 是 USED, 代表用户操作是使用当前的优惠券, 影响到两个 Cache
        // USABLE（删除）, USED（新增）

        log.debug("Add Coupon To Cache For Used.");

        Map<String, String> needCachedForUsed = new HashMap<>(coupons.size());

        String redisKeyForUsable = status2RedisKey(memberCode, CouponStatusEnum.USABLE.getStatus());
        String redisKeyForUsed = status2RedisKey(memberCode, CouponStatusEnum.USED.getStatus());

        // 获取当前用户可用的优惠券
        List<Coupon> curUsableCoupons = getCachedCoupons(memberCode, CouponStatusEnum.USABLE.getStatus());
        // 当前可用的优惠券个数一定是大于已使用优惠券个数的
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> needCachedForUsed.put(c.getId().toString(), JSON.toJSONString(c)));

        // 校验当前的优惠券参数是否与 Cached 中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> needCleanIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());

        if (!CollectionUtils.isSubCollection(needCleanIds, curUsableIds)) {
            log.error("CurCoupons Is Not Equal ToCache: {}, {}, {}", memberCode, JSON.toJSONString(curUsableIds), JSON.toJSONString(needCleanIds));
            throw new CouponException("CurCoupons Is Not Equal To Cache!");
        }

        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations operations) throws DataAccessException {

                // 1. 已使用的优惠券 Cache 缓存添加
                operations.opsForHash().putAll(redisKeyForUsed, needCachedForUsed);
                // 2. 可用的优惠券 Cache 需要清理
                operations.opsForHash().delete(redisKeyForUsable, needCleanIds.stream().map(Functions.toStringFunction()).collect(Collectors.toList()));
                // 3. 重置过期时间
                operations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                operations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

                return null;
            }
        };

        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }

    /**
     * 将过期优惠券加入到 Cache 中
     */
    private void addCouponToCacheForExpired(String memberCode, List<Coupon> coupons) throws CouponException {

        // status 是 EXPIRED, 代表是已有的优惠券过期了, 影响到两个 Cache
        // USABLE（删除）, EXPIRED（新增）

        log.debug("Add Coupon To Cache For Expired.");

        // 最终需要保存的 Cache
        Map<String, String> needCachedForExpired = new HashMap<>(coupons.size());

        String redisKeyForUsable = status2RedisKey(memberCode, CouponStatusEnum.USABLE.getStatus());
        String redisKeyForExpired = status2RedisKey(memberCode, CouponStatusEnum.EXPIRED.getStatus());

        List<Coupon> curUsableCoupons = getCachedCoupons(memberCode, CouponStatusEnum.USABLE.getStatus());

        // 当前可用的优惠券个数一定是大于1的
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> needCachedForExpired.put(c.getId().toString(), JSON.toJSONString(c)));

        // 校验当前的优惠券参数是否与 Cached 中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons Is Not Equal To Cache: {}, {}, {}", memberCode, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon Is Not Equal To Cache.");
        }

        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations operations) throws DataAccessException {

                // 1. 已过期的优惠券 Cache 缓存
                operations.opsForHash().putAll(redisKeyForExpired, needCachedForExpired);
                // 2. 可用的优惠券 Cache 需要清理
                operations.opsForHash().delete(redisKeyForUsable, paramIds.stream().map(Functions.toStringFunction()).collect(Collectors.toList()));
                // 3. 重置过期时间
                operations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                operations.expire(redisKeyForExpired, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

                return null;
            }
        };

        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }

    /**
     * 获取一个随机的过期时间
     * 缓存雪崩: key 在同一时间失效
     *
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回 [min, max] 之间的随机秒数
     */
    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(min * 60 * 60, max * 60 * 60);
    }
}
