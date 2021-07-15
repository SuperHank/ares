package com.hank.ares.thymeleaf;

import com.alibaba.fastjson.JSON;
import com.hank.ares.dao.CouponTemplateDao;
import com.hank.ares.enums.coupon.*;
import com.hank.ares.model.CouponTemplate;
import com.hank.ares.model.TemplateRule;
import com.hank.ares.model.dto.req.CreateTemplateReqDto;
import com.hank.ares.service.ICouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 优惠券模板 Controller
 */
@Slf4j
@Controller
@RequestMapping("/template/thy")
public class ThyTemplateController {

    /**
     * CouponTemplate Dao
     */
    private final CouponTemplateDao templateDao;

    /**
     * 构造优惠券模板服务
     */
    private final ICouponTemplateService templateService;

    @Autowired
    public ThyTemplateController(CouponTemplateDao templateDao, ICouponTemplateService templateService) {
        this.templateDao = templateDao;
        this.templateService = templateService;
    }

    /**
     * 优惠券系统入口
     */
    @GetMapping("/home")
    public String home() {

        log.info("view home.");
        return "home";
    }

    /**
     * 查看优惠券模板详情
     */
    @GetMapping("/info/{id}")
    public String info(@PathVariable Integer id, ModelMap map) {

        log.info("view template info.");
        Optional<CouponTemplate> templateO = templateDao.findById(id);
        if (templateO.isPresent()) {
            CouponTemplate template = templateO.get();
            map.addAttribute("template", ThyTemplateInfo.to(template));
        }

        return "template_detail";
    }

    /**
     * 查看优惠券模板列表
     */
    @GetMapping("/list")
    public String list(ModelMap map) {

        log.info("view template list.");
        List<CouponTemplate> couponTemplates = templateDao.findAll();
        List<ThyTemplateInfo> templates =
                couponTemplates.stream().map(ThyTemplateInfo::to).collect(Collectors.toList());

        map.addAttribute("templates", templates);
        return "template_list";
    }

    /**
     * 创建优惠券模板
     */
    @GetMapping("/create")
    public String create(ModelMap map, HttpSession session) {

        log.info("view create form.");

        session.setAttribute("category", CouponCategoryEnum.values());
        session.setAttribute("productLine", ProductLineEnum.values());
        session.setAttribute("target", DistributeTargetEnum.values());
        session.setAttribute("period", PeriodTypeEnum.values());
        session.setAttribute("goodsType", GoodsTypeEnum.values());

        map.addAttribute("template", new ThyCreateTemplate());
        map.addAttribute("action", "create");
        return "template_form";
    }

    /**
     * 创建优惠券模板
     */
    @PostMapping("/create")
    public String create(@ModelAttribute ThyCreateTemplate template) throws Exception {

        log.info("create form.");
        log.info("{}", JSON.toJSONString(template));

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                template.getPeriod(), template.getGap(),
                new SimpleDateFormat("yyyy-MM-dd").parse(template.getDeadline()).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(template.getQuota(), template.getBase()));
        rule.setLimitation(template.getLimitation());
        rule.setUsage(new TemplateRule.Usage(template.getProvince(), template.getCity(),
                JSON.toJSONString(template.getGoodsType())));
        rule.setWeight(
                JSON.toJSONString(Stream.of(template.getWeight().split(",")).collect(Collectors.toList()))
        );

        CreateTemplateReqDto request = new CreateTemplateReqDto(
                template.getName(), template.getLogo(), template.getDesc(),
                template.getCategory(), template.getProductLine(), template.getCount(),
                template.getUserId(), template.getTarget(), rule
        );

        log.info("create coupon template: {}", JSON.toJSONString(templateService.buildTemplate(request)));

        return "redirect:/template/thy/list";
    }
}
