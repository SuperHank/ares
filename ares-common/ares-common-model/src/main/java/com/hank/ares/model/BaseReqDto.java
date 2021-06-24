package com.hank.ares.model;

import lombok.Data;

@Data
public abstract class BaseReqDto {
    private String token;

    protected abstract boolean validate();
}
