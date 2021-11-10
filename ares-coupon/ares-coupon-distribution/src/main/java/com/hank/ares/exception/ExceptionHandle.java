package com.hank.ares.exception;

import com.alibaba.fastjson.JSON;
import com.hank.ares.enums.common.ResultCode;
import com.hank.ares.model.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(AresBusinessException.class)
    @ResponseBody
    public String handleSzBusinessException(AresBusinessException e) {
        return JSON.toJSONString(new CommonResponse<>(ResultCode.SYSTEM_ERROR.getCode(), e.getResMsg(), null));
    }
}
