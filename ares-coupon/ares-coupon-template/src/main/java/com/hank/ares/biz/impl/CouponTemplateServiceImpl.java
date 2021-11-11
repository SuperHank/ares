package com.hank.ares.biz.impl;

import com.hank.ares.dao.CouponTemplateDao;
import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.dto.req.CreateTemplateReqDto;
import com.hank.ares.biz.service.IAsyncService;
import com.hank.ares.biz.service.ICouponTemplateService;
import com.hank.ares.util.ExceptionThen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 优惠券模板表 服务实现类
 */
@Service
@Slf4j
public class CouponTemplateServiceImpl implements ICouponTemplateService {

    @Autowired
    private CouponTemplateDao templateDao;
    @Autowired
    private IAsyncService asyncService;

    @Override
    @Transactional
    public CouponTemplate buildTemplate(CreateTemplateReqDto reqDto) {
        // 判断同名的优惠券模板是否存在
        ExceptionThen.then(templateDao.findByName(reqDto.getName()) != null, ResultCode.PARAM_DUPLICATED, "Exist Same Name Template!");

        // 构造 CouponTemplate 并保存到数据库中
        CouponTemplate template = templateDao.save(requestToTemplate(reqDto));

        // 根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }

    /**
     * 将 TemplateRequest 转换为 CouponTemplate
     */
    private CouponTemplate requestToTemplate(CreateTemplateReqDto reqDto) {

        return new CouponTemplate(
                reqDto.getName(),
                reqDto.getLogo(),
                reqDto.getDesc(),
                reqDto.getCategory(),
                reqDto.getProductLine(),
                reqDto.getCount(),
                reqDto.getUserId(),
                reqDto.getTarget(),
                reqDto.getRule()
        );
    }
}
