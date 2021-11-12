package com.hank.ares.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hank.ares.biz.service.ICouponService;
import com.hank.ares.biz.service.IRedisService;
import com.hank.ares.client.coupon.CouponSettlementClient;
import com.hank.ares.client.coupon.CuoponTemplateClient;
import com.hank.ares.constant.KafkaTopicConstants;
import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.enums.coupon.CouponStatusEnum;
import com.hank.ares.exception.CouponException;
import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.CouponDto;
import com.hank.ares.model.CouponTemplateDto;
import com.hank.ares.model.dto.req.AcquireTemplateReqDto;
import com.hank.ares.model.kafka.CouponKafkaMsg;
import com.hank.ares.model.settlement.SettlementDto;
import com.hank.ares.model.vo.CouponClassify;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 优惠券(用户领取的记录) 服务实现类
 */
@Slf4j
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private IRedisService redisService;

    @Autowired
    private CuoponTemplateClient cuoponTemplateClient;

    @Autowired
    private CouponSettlementClient couponSettlementClient;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
    @Transactional
    public Coupon acquireTemplate(AcquireTemplateReqDto request) throws CouponException {
        CouponTemplateDto couponTemplateDto = cuoponTemplateClient.getTemplateByTemplateCode(request.getTemplateCode());
        // 优惠券模板是需要存在的
        ExceptionThen.then(couponTemplateDto == null, ResultCode.DATA_NOT_EXIST, "Can Not Acquire Template From TemplateClient");

        // 用户是否可以领取这张优惠券
        List<Coupon> usableCoupons = findCouponsByStatus(request.getMemberCode(), CouponStatusEnum.USABLE.getStatus());
        Map<String, List<Coupon>> templateId2Coupons = usableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateCode));

        // 已领取该模版下的优惠券，且优惠券数量已到优惠券领取上限
        if (templateId2Coupons.containsKey(couponTemplateDto.getId())
                && templateId2Coupons.get(couponTemplateDto.getId()).size() >= couponTemplateDto.getRule().getLimitation()) {
            log.error("Exceed Template Assign Limitation: {}", couponTemplateDto.getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }

        // 尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(couponTemplateDto.getId());
        if (StringUtils.isBlank(couponCode)) {
            log.error("Can Not Acquire Coupon Code:{}", couponTemplateDto.getId());
            throw new CouponException("Can Not Acquire Coupon Code");
        }

        // 填充 Coupon 对象的 CouponTemplateSDK, 一定要在放入缓存之前去填充
        Coupon newCoupon = new Coupon(couponTemplateDto.getTemplateCode(), request.getMemberCode(), couponCode, CouponStatusEnum.USABLE);
        couponMapper.insert(newCoupon);
        newCoupon.setTemplateSDK(couponTemplateDto);

        // 放入缓存中
        redisService.addCouponToCache(request.getMemberCode(), Collections.singletonList(newCoupon), CouponStatusEnum.USABLE.getStatus());

        return newCoupon;
    }

    /**
     * 根据用户 id 和状态查询优惠券记录
     *
     * @param memberCode 用户 id
     * @param status     优惠券状态
     * @return {@link Coupon}s
     * @throws CouponException
     */
    @Override
    public List<Coupon> findCouponsByStatus(String memberCode, Integer status) throws CouponException {
        List<Coupon> curCached = redisService.getCachedCoupons(memberCode, status);
        List<Coupon> preTarget;
        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("coupon cache is not empty:{},{}", memberCode, status);
            preTarget = curCached;
        } else {
            log.debug("coupon cache is empty, get coupon from db:{},{}", memberCode, status);
            QueryWrapper<Coupon> wrapper = new QueryWrapper<Coupon>().eq("member_code", memberCode).eq("status", status);
            List<Coupon> dbCoupons = couponMapper.selectList(wrapper);
            // 如果数据库中没有记录, 直接返回就可以, Cache 中已经加入了一张无效的优惠券
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not have coupon:{},{}", memberCode, status);
                return Collections.emptyList();
            }

            // 填充 dbCoupons的 templateSDK 字段
            Map<String, CouponTemplateDto> id2TemplateSDK = cuoponTemplateClient.getTemplateByTemplateCodes(
                    dbCoupons.stream().map(Coupon::getTemplateCode).collect(Collectors.toList()));
            dbCoupons.forEach(dc -> dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateCode())));
            // 数据库中存在记录
            preTarget = dbCoupons;
            // 将记录写入cache
            redisService.addCouponToCache(memberCode, preTarget, status);
        }
        // 将无效优惠券剔除
        preTarget = preTarget.stream().filter(c -> c.getId() != -1).collect(Collectors.toList());
        preTarget.forEach(i -> {
            CouponTemplateDto templateSdk = cuoponTemplateClient.getTemplateByTemplateCode(i.getTemplateCode());
            i.setTemplateSDK(templateSdk);
        });
        // 如果当前获取的是可用优惠券, 还需要做对已过期优惠券的延迟处理
        if (CouponStatusEnum.of(status) == CouponStatusEnum.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            // 如果已过期状态不为空, 需要做延迟处理
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupons To Cache From FindCouponsByStatus: {}, {}", memberCode, status);
                redisService.addCouponToCache(memberCode, classify.getExpired(), CouponStatusEnum.EXPIRED.getStatus());
                // 发送到 kafka 中做异步处理
                CouponKafkaMsg couponKafkaMsg = new CouponKafkaMsg(CouponStatusEnum.EXPIRED.getStatus(),
                        classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList()));
                kafkaTemplate.send(KafkaTopicConstants.TOPIC, JSON.toJSONString(couponKafkaMsg));
            }

            return classify.getUsable();
        }
        return preTarget;
    }

    /**
     * 根据用户 id 查找当前可以领取的优惠券模板
     *
     * @param memberCode 用户 id
     * @return {@link CouponTemplateDto}s
     */
    @Override
    public List<CouponTemplateDto> findAvailableTemplate(String memberCode) throws CouponException {
        long curTime = new Date().getTime();
        List<CouponTemplateDto> templateSDKS = cuoponTemplateClient.getAllUsableTemplate();
        log.debug("Find All Template(From TemplateClient) Count:{}", templateSDKS.size());

        templateSDKS = templateSDKS.stream().filter(i -> i.getRule().getExpiration().getDeadline() > curTime).collect(Collectors.toList());

        log.info("Find Usable Template Count: {}", templateSDKS.size());

        HashMap<Integer, Pair<Integer, CouponTemplateDto>> limit2Template = new HashMap<>(templateSDKS.size());
        // 模版ID：{数量限制,模版信息}
        templateSDKS.forEach(i -> limit2Template.put(i.getId(), Pair.of(i.getRule().getLimitation(), i)));


        // key 是 TemplateId
        List<Coupon> userUsableCoupons = findCouponsByStatus(memberCode, CouponStatusEnum.USABLE.getStatus());
        log.debug("Current User Has Usable Coupons: {}, {}", memberCode, userUsableCoupons.size());
        Map<String, List<Coupon>> acquiredTemplateId2Coupons = userUsableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateCode));

        List<CouponTemplateDto> result = new ArrayList<>();
        // 根据 Template 的 Rule 判断是否可以领取优惠券模板
        limit2Template.forEach((templateId, v) -> {
            // 优惠券领取上线
            int limitation = v.getLeft();

            if (acquiredTemplateId2Coupons.containsKey(templateId) && acquiredTemplateId2Coupons.get(templateId).size() >= limitation) {
                return;
            }
            result.add(v.getRight());
        });

        return result;
    }

    /**
     * 结算(核销)优惠券
     * 这里需要注意, 规则相关处理需要由 Settlement 系统去做, 当前系统仅仅做
     * 业务处理过程(校验过程)
     *
     * @param settlementDto {@link SettlementDto}
     * @return {@link SettlementDto}
     */
    @Override
    public SettlementDto settlement(SettlementDto settlementDto) throws CouponException {
        // 当没有传递优惠券时, 直接返回商品总价
        List<SettlementDto.CouponAndTemplateId> ctInfos = settlementDto.getCouponAndTemplateIds();
        if (CollectionUtils.isEmpty(ctInfos)) {
            log.info("Empty Coupons For Settle.");

            double goodsSum = settlementDto.getGoodsDtos().stream().mapToDouble(i -> i.getPrice() * i.getCount()).sum();

            // 没有优惠券也就不存在优惠券的核销, SettlementInfo 其他的字段不需要修改
            settlementDto.setCost(AmtUtil.retain2Decimals(goodsSum));
        }

        // 校验传递的优惠券是否是用户自己的
        List<Coupon> coupons = findCouponsByStatus(settlementDto.getMemberCode(), CouponStatusEnum.USABLE.getStatus());
        Map<Integer, Coupon> id2Coupon = coupons.stream().collect(Collectors.toMap(Coupon::getId, Function.identity()));
        if (MapUtils.isEmpty(id2Coupon) || !CollectionUtils.isSubCollection(ctInfos.stream().map(SettlementDto.CouponAndTemplateId::getId).collect(Collectors.toList()), id2Coupon.keySet())) {
            log.info("{}", id2Coupon.keySet());
            log.info("{}", ctInfos.stream().map(SettlementDto.CouponAndTemplateId::getId).collect(Collectors.toList()));
            log.error("User Coupon Has Some Problem, It Is Not SubCollection Of Coupons!");
            throw new CouponException("User Coupon Has Some Problem, It Is Not SubCollection Of Coupons!");
        }

        log.debug("Current Settlement Coupons Is User's: {}", ctInfos.size());

        List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
        ctInfos.forEach(ci -> settleCoupons.add(id2Coupon.get(ci.getId())));

        // 通过结算服务获取结算信息
        SettlementDto processedInfo = couponSettlementClient.computeRule(settlementDto);
        if (processedInfo.getEmploy() && CollectionUtils.isNotEmpty(processedInfo.getCouponAndTemplateIds())) {
            log.info("Settle User Coupon: {}, {}", settlementDto.getMemberCode(), JSON.toJSONString(settleCoupons));
            // 更新缓存
            redisService.addCouponToCache(settlementDto.getMemberCode(), settleCoupons, CouponStatusEnum.USED.getStatus());
            // 更新 db
            String msg = JSON.toJSONString(new CouponKafkaMsg(CouponStatusEnum.USED.getStatus(), settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList())));
            kafkaTemplate.send(KafkaTopicConstants.TOPIC, msg);
        }

        return processedInfo;
    }

    @Override
    public List<CouponDto> getByUserId(String memberCode) {
        return cuoponTemplateClient.getCouponByMemberCode(memberCode);
    }
}
