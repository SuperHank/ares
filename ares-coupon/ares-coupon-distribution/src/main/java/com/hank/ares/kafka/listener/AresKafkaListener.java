package com.hank.ares.kafka.listener;

import com.alibaba.fastjson.JSON;
import com.hank.ares.constants.KafkaTopicConstants;
import com.hank.ares.enums.biz.coupon.CouponStatusEnum;
import com.hank.ares.mapper.CouponMapper;
import com.hank.ares.model.Coupon;
import com.hank.ares.model.kafka.CouponKafkaMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class AresKafkaListener {

    @Autowired
    private CouponMapper couponMapper;

    @KafkaListener(topics = {KafkaTopicConstants.TOPIC}, groupId = "ares-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMsg couponInfo = JSON.parseObject(message.toString(), CouponKafkaMsg.class);
            log.info("Receive CouponKafkaMessage:{}", JSON.toJSONString(couponInfo));

            CouponStatusEnum status = CouponStatusEnum.of(couponInfo.getStatus());

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
    private void processCouponsByStatus(CouponKafkaMsg kafkaMessage, CouponStatusEnum status) {
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
