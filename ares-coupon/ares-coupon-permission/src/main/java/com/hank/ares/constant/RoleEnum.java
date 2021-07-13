package com.hank.ares.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN("管理员"),
    SUPER_ADMIN("超级管理员"),
    CUSTOMER("普通用户");

    private final String roleName;
}
