package com.hank.ares.util;

import com.hank.ares.exception.AresBusinessException;

public class ExceptionThen {
    public static void then(boolean condition, String code, String msg) {
        if (condition) {
            throw new AresBusinessException(code, msg, false);
        }
    }

    public static void then(boolean condition, String bizName, Throwable cause) {
        if (condition) {
            throw new AresBusinessException(cause, bizName);
        }
    }
}
