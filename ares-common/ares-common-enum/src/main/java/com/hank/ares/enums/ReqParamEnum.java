package com.hank.ares.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReqParamEnum {

    /**
     * 用户登陆id
     */
    ARES_USER_ID("ares-user-id"),
    /**
     * 用户登陆token
     */
    ARES_TOKEN("ares-user-token");

    private String paramKey;
}
