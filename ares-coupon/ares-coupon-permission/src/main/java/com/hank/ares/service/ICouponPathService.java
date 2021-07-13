package com.hank.ares.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hank.ares.model.CouponPath;
import com.hank.ares.vo.CreatePathRequest;

import java.util.List;

/**
 * 路径信息表 服务类
 */
public interface ICouponPathService extends IService<CouponPath> {

    /**
     * 添加新的 path 到数据表中
     *
     * @param request {@link CreatePathRequest}
     * @return Path 数据记录的主键
     */
    List<Integer> createPath(CreatePathRequest request);
}
