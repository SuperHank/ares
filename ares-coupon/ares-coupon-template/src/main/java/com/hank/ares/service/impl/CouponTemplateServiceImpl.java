package com.hank.ares.service.impl;

import com.hank.ares.dao.CouponTemplateDao;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.dto.req.CreateTemplateReqDto;
import com.hank.ares.service.IAsyncService;
import com.hank.ares.service.ICouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优惠券模板表 服务实现类
 * </p>
 *
 * @author shih
 * @since 2021-06-23
 */
@Service
@Slf4j
public class CouponTemplateServiceImpl implements ICouponTemplateService {

    @Autowired
    private CouponTemplateDao templateDao;
    @Autowired
    private IAsyncService asyncService;

    @Override
    public CouponTemplate buildTemplate(CreateTemplateReqDto reqDto) throws CouponException {
        // 判断同名的优惠券模板是否存在
        if (null != templateDao.findByName(reqDto.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }

        // 构造 CouponTemplate 并保存到数据库中
        CouponTemplate template = requestToTemplate(reqDto);
        template = templateDao.save(template);

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
