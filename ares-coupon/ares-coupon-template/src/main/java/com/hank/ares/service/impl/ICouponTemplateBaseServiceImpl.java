package com.hank.ares.service.impl;

import com.hank.ares.dao.CouponTemplateDao;
import com.hank.ares.exception.CouponException;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.CouponTemplateSDK;
import com.hank.ares.service.ICouponTemplateBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ICouponTemplateBaseServiceImpl implements ICouponTemplateBaseService {

    @Autowired
    private CouponTemplateDao couponTemplateDao;


    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     *
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = couponTemplateDao.findById(id);
        if (!template.isPresent()) {
            throw new CouponException("Template does not exist: " + id);
        }
        return template.get();
    }

    /**
     * 查找所有可用的优惠券模板
     *
     * @return {@link CouponTemplateSDK}s
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        List<CouponTemplate> templates = couponTemplateDao.findAllByAvailableAndExpired(true, false);
        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toList());
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
                template.getProductLine().getCode(),
                template.getTemplateKey(),  // 并不是拼装好的 Template Key
                template.getTarget().getCode(),
                template.getRule()
        );
    }

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     *
     * @param ids 模板 ids
     * @return Map<key: 模板 id, value: CouponTemplateSDK>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = couponTemplateDao.findAllById(ids);

        return templates.stream().map(this::template2TemplateSDK)
                .collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
    }
}
