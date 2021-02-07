package com.hank.ares.model;

import com.hank.ares.enums.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Result implements Serializable {
    private Integer code;
    private String message;
    private Object data;


    private Result(ResultCode redultCode, Object data) {
        this.code = redultCode.getCode();
        this.message = redultCode.getMessage();
        this.data = data;
    }

    private Result(ResultCode redultCode) {
        this.code = redultCode.getCode();
        this.message = redultCode.getMessage();
    }

    public static Result success() {
        return new Result(ResultCode.SUCCESS);
    }

    public static Result success(Object data) {
        return new Result(ResultCode.SUCCESS, data);
    }

    public static Result success(ResultCode resultCode) {
        return new Result(resultCode);
    }

    public static Result success(ResultCode resultCode, Object data) {
        return new Result(resultCode, data);
    }

    public static Result fail(ResultCode resultCode) {
        return new Result(resultCode);
    }

    public static Result fail(ResultCode resultCode, Object data) {
        return new Result(resultCode, data);
    }
}
