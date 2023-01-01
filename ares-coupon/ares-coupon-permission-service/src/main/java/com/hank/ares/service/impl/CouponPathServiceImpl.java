package com.hank.ares.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hank.ares.mapper.CouponPathMapper;
import com.hank.ares.model.CouponPath;
import com.hank.ares.model.coupon.permission.CreatePathReqDto;
import com.hank.ares.service.ICouponPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 路径信息表 服务实现类
 */
@Service
public class CouponPathServiceImpl extends ServiceImpl<CouponPathMapper, CouponPath> implements ICouponPathService {

    @Autowired
    private CouponPathMapper pathMapper;

    @Override
    @Transactional
    public void createPath(CreatePathReqDto request) {
        for (CreatePathReqDto.PathInfo pathInfo : request.getPathInfos()) {
            QueryWrapper<CouponPath> wrapper = new QueryWrapper<>();
            wrapper.eq("path_pattern", pathInfo.getPathPattern())
                    .eq("http_method", pathInfo.getHttpMethod())
                    .eq("path_name", pathInfo.getPathName())
                    .eq("service_name", pathInfo.getServiceName())
                    .eq("op_mode", pathInfo.getOpMode());

            CouponPath couponPath = pathMapper.selectOne(wrapper);
            if (couponPath == null) {
                CouponPath insertDo = new CouponPath(pathInfo.getPathPattern(), pathInfo.getHttpMethod(), pathInfo.getPathName(), pathInfo.getServiceName(), pathInfo.getOpMode());
                pathMapper.insert(insertDo);
            }
        }
    }
}
