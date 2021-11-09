package com.hank.ares.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(1, "成功"),
    /**
     * 1001-1999
     * 参数错误
     */
    PARAM_IS_INVALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),
    PARAM_DUPLICATED(1005, "数据重复"),

    /**
     * 用户错误
     * 2001-2999
     */
    USER_NOT_LOGGED_IN(2001, "用户未登录，访问的路径需要验证，请登录"),
    USER_LOGIN_ERROR(2002, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(2003, "账号已被禁用"),
    USER_NOT_EXIST(2004, "用户不存在"),
    USER_HAS_EXISTED(2005, "用户已存在"),

    /**
     * 数据错误
     * 3001-3999
     */
    DATA_NOT_EXIST(3001, "数据不存在"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(9000, "系统异常"),
    ;

    private final Integer code;
    private final String message;
}
