package com.hank.ares.controller;


import com.hank.ares.model.AddSettlementMemberReqDto;
import com.hank.ares.model.AresSettlementMember;
import com.hank.ares.service.IAresSettlementMemberService;
import com.hank.ares.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author shih
 * @since 2021-02-07
 */
@RestController
@RequestMapping("/ares-settlement-member")
@ApiModel("ares_settlement-会员中心")
public class AresSettlementMemberController {

    @Value("${spring.profiles.active}")
    private String prifile;

    @Autowired
    private IAresSettlementMemberService settlementMemberService;

    @PostMapping("/add")
    @ApiOperation("新增会员")
    private String add(@RequestBody AddSettlementMemberReqDto reqDto) {

        AresSettlementMember insertDo = new AresSettlementMember();
        BeanUtils.copyProperties(reqDto, insertDo);
        insertDo.setMemberCode(String.format("%s%s", "SMC", CommonUtil.getRandomNumCode(10)));
        insertDo.setCreater(prifile);
        boolean save = settlementMemberService.save(insertDo);
        return save ? "保存成功" : "保存失败";
    }
}
