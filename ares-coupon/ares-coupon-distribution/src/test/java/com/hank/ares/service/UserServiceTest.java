package com.hank.ares.service;

import com.alibaba.fastjson.JSON;
import com.hank.ares.CouponDistributionApplication;
import com.hank.ares.enums.CouponStatus;
import com.hank.ares.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CouponDistributionApplication.class})
public class UserServiceTest {
    /**
     * fake 一个 UserId
     */
    private Long fakeUserId = 20001L;

    @Qualifier("userServiceImpl")
    @Autowired
    private IUserService userService;

    @Test
    public void testFindCouponByStatus() throws CouponException {
        System.out.println(JSON.toJSONString(userService.findCouponsByStatus(fakeUserId, CouponStatus.USABLE.getStatus())));
        System.out.println(JSON.toJSONString(userService.findCouponsByStatus(fakeUserId, CouponStatus.USED.getStatus())));
        System.out.println(JSON.toJSONString(userService.findCouponsByStatus(fakeUserId, CouponStatus.EXPIRED.getStatus())));
    }

    @Test
    public void testFindAvailableTemplate() throws CouponException {
        userService.findAvailableTemplate(fakeUserId);
    }
}
