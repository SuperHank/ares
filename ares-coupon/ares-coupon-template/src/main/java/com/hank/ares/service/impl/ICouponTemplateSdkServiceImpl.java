package com.hank.ares.service.impl;

import com.hank.ares.dao.CouponTemplateDao;
import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.CouponTemplateSDK;
import com.hank.ares.service.ICouponTemplateSdkService;
import com.hank.ares.util.ExceptionThen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ICouponTemplateSdkServiceImpl implements ICouponTemplateSdkService {

    @Autowired
    private CouponTemplateDao couponTemplateDao;

    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     *
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplateSDK getById(Integer id) {
        Optional<CouponTemplate> template = couponTemplateDao.findById(id);
        ExceptionThen.then(!template.isPresent(), ResultCode.DATA_NOT_EXIST, "Template Does Not Exist: " + id);
        return template2TemplateSDK(template.get());
    }

    /**
     * 查找所有可用的优惠券模板
     *
     * @return {@link CouponTemplateSDK}s
     */
    @Override
    public List<CouponTemplateSDK> getAllUsableTemplate() {
        List<CouponTemplate> templates = couponTemplateDao.findAllByAvailableAndExpired(true, false);
        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     *
     * @param ids 模板 ids
     * @return Map<key: 模板 id, value: CouponTemplateSDK>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> getByIds(Collection<Integer> ids) {
        List<CouponTemplate> templates = couponTemplateDao.findAllById(ids);
        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
    }

    /**
     * 将 CouponTemplate 转换为 CouponTemplateSDK
     */
    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template) {

        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getIntro(),
                template.getCategory().getCode(),
                template.getProductLineEnum().getCode(),
                template.getTemplateKey(),  // 并不是拼装好的 Template Key
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
