package com.hank.ares.service.impl;

import com.alibaba.fastjson.JSON;
import com.hank.ares.constant.CouponConstant;
import com.hank.ares.enums.CouponStatus;
import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.vo.CouponKafkaMessage;
import com.hank.ares.service.IKafkaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class KafkaServiceImpl implements IKafkaService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    @KafkaListener(topics = {CouponConstant.TOPIC}, groupId = "ares-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(message.toString(), CouponKafkaMessage.class);
            log.info("Receive CouponKafkaMessage:{}", message.toString());

            CouponStatus status = CouponStatus.of(couponInfo.getStatus());

            switch (status) {
                case USABLE:
                    break;
                case USED:
                case EXPIRED:
                    processCouponsByStatus(couponInfo, status);
                    break;
            }
        }
    }

    /**
     * 根据状态处理优惠券信息
     */
    private void processCouponsByStatus(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        List<Coupon> coupons = couponMapper.selectBatchIds(kafkaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons) || coupons.size() != kafkaMessage.getIds().size()) {
            log.error("Can Not Find Right Coupon Info: {}", JSON.toJSONString(kafkaMessage));
            // TODO 发送邮件
            return;
        }

        AtomicInteger count = new AtomicInteger();
        coupons.forEach(coupon -> {
            coupon.setStatus(status.getStatus());
            count.addAndGet(couponMapper.updateById(coupon));
        });
        log.info("CouponKafkaMessage Op Coupon Count: {}", count.get());
    }
}
