package com.hank.ares.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 分发目标
 */
@Getter
@AllArgsConstructor
public enum DistributeTarget {

    SINGLE(1, "单用户"),
    MULTI(2, "多用户");

    /**
     * 分发目标编码
     */
    private final Integer code;
    /**
     * 分发目标描述
     */
    private final String description;

    public static DistributeTarget of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(i -> i.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists!"));
    }
}
