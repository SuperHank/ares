package com.hank.ares.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hank.ares.enums.permission.RoleEnum;
import com.hank.ares.mapper.CouponPathMapper;
import com.hank.ares.mapper.CouponRoleMapper;
import com.hank.ares.mapper.CouponRolePathMappingMapper;
import com.hank.ares.mapper.CouponUserRoleMappingMapper;
import com.hank.ares.model.CouponPath;
import com.hank.ares.model.CouponRole;
import com.hank.ares.model.CouponRolePathMapping;
import com.hank.ares.model.CouponUserRoleMapping;
import com.hank.ares.service.IPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private CouponRoleMapper roleMapper;
    @Autowired
    private CouponPathMapper pathMapper;
    @Autowired
    private CouponUserRoleMappingMapper userRoleMappingMapper;
    @Autowired
    private CouponRolePathMappingMapper rolePathMappingMapper;

    @Override
    public boolean checkPermission(Integer userId, String uri, String httpMethod) {
        QueryWrapper<CouponUserRoleMapping> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        CouponUserRoleMapping userRoleMapping = userRoleMappingMapper.selectOne(query);

        // 用户没有绑定角色, 直接返回 false
        if (userRoleMapping == null) {
            log.error("userId not exist is UserRoleMapping: {}", userId);
            return false;
        }

        // 如果找不到对应的 Role 记录, 直接返回 false
        CouponRole role = roleMapper.selectById(userRoleMapping.getRoleId());
        if (role == null) {
            log.error("roleId not exist in Role: {}", userRoleMapping.getRoleId());
            return false;
        }

        // 如果用户角色是超级管理员, 直接返回 true
        if (RoleEnum.SUPER_ADMIN.name().equals(role.getRoleTag())) {
            return true;
        }

        // 如果路径没有注册(忽略了), 直接返回 true
        QueryWrapper<CouponPath> wrapper = new QueryWrapper<>();
        wrapper.eq("path_pattern", uri).eq("http_method", httpMethod);
        CouponPath path = pathMapper.selectOne(wrapper);
        if (path == null) {
            log.error("path not exist : {}", JSON.toJSONString(path));
            return false;
        }

        QueryWrapper<CouponRolePathMapping> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", role.getId()).eq("path_id", path.getId());
        CouponRolePathMapping rolePathMapping = rolePathMappingMapper.selectOne(queryWrapper);

        return rolePathMapping != null;
    }

}
