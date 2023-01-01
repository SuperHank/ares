package com.hank.ares.api.impl;

import com.hank.ares.api.service.ICouponTemplateApiService;
import com.hank.ares.dao.CouponTemplateDao;
import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.coupon.template.CouponTemplateDto;
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
public class CouponTemplateApiServiceImpl implements ICouponTemplateApiService {

    @Autowired
    private CouponTemplateDao couponTemplateDao;

    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     *
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplateDto getById(Integer id) {
        Optional<CouponTemplate> template = couponTemplateDao.findById(id);
        ExceptionThen.then(!template.isPresent(), ResultCode.DATA_NOT_EXIST, "Template Does Not Exist: " + id);
        return template2TemplateSDK(template.get());
    }

    /**
     * 查找所有可用的优惠券模板
     *
     * @return {@link CouponTemplateDto}s
     */
    @Override
    public List<CouponTemplateDto> getAllUsableTemplate() {
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
    public Map<Integer, CouponTemplateDto> getByIds(Collection<Integer> ids) {
        List<CouponTemplate> templates = couponTemplateDao.findAllById(ids);
        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toMap(CouponTemplateDto::getId, Function.identity()));
    }

    /**
     * 将 CouponTemplate 转换为 CouponTemplateSDK
     */
    private CouponTemplateDto template2TemplateSDK(CouponTemplate template) {

        return new CouponTemplateDto(
                template.getId(),
                template.getTemplateCode(),
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

    /**
     * 根据模版编号查询模版信息
     *
     * @param templateCode
     * @return
     */
    @Override
    public CouponTemplateDto getByTemplateCode(String templateCode) {
        CouponTemplate template = couponTemplateDao.findByTemplateCode(templateCode);
        return template2TemplateSDK(template);
    }

    /**
     * 批量查询模版信息
     *
     * @param templateCodes
     * @return
     */
    @Override
    public Map<String, CouponTemplateDto> getByTemplateCodes(Collection<String> templateCodes) {

        List<CouponTemplate> templates = couponTemplateDao.findAllByTemplateCode(templateCodes);
        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toMap(CouponTemplateDto::getTemplateCode, Function.identity()));
    }
}
