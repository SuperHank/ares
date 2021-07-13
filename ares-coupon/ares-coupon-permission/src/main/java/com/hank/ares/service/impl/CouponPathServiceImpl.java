package com.hank.ares.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hank.ares.mapper.CouponPathMapper;
import com.hank.ares.model.CouponPath;
import com.hank.ares.service.ICouponPathService;
import com.hank.ares.vo.CreatePathRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 路径信息表 服务实现类
 */
@Service
public class CouponPathServiceImpl extends ServiceImpl<CouponPathMapper, CouponPath> implements ICouponPathService {

    @Autowired
    private CouponPathMapper pathMapper;


    @Override
    public List<Integer> createPath(CreatePathRequest request) {
        List<CreatePathRequest.PathInfo> pathInfos = request.getPathInfos();
        List<CreatePathRequest.PathInfo> validRequests = new ArrayList<>(request.getPathInfos().size());

        QueryWrapper<CouponPath> wrapper = new QueryWrapper<>();
        wrapper.eq("service_name", request.getPathInfos().get(0).getServiceName());
        List<CouponPath> currentPaths = pathMapper.selectList(wrapper);
        String serviceName = pathInfos.get(0).getServiceName();

        if (CollectionUtils.isNotEmpty(currentPaths)) {
            for (CreatePathRequest.PathInfo pathInfo : pathInfos) {
                boolean isValid = true;
                for (CouponPath currentPath : currentPaths) {
                    if (currentPath.getPathPattern().equals(pathInfo.getPathPattern())
                            && currentPath.getHttpMethod().equals(pathInfo.getHttpMethod())) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    validRequests.add(pathInfo);
                }
            }
        } else {
            validRequests = pathInfos;
        }

        List<CouponPath> paths = new ArrayList<>(validRequests.size());
        validRequests.forEach(p -> paths.add(new CouponPath(
                p.getPathPattern(),
                p.getHttpMethod(),
                p.getPathName(),
                p.getServiceName(),
                p.getOpMode()
        )));

        ArrayList<Integer> ids = new ArrayList<>();
        for (CouponPath path : paths) {
            pathMapper.insert(path);
            ids.add(path.getId());
        }
        return ids;
    }
}
