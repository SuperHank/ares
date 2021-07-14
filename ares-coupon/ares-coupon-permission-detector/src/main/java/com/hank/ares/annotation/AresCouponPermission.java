package com.hank.ares.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限描述注解: 定义 Controller 接口的权限
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AresCouponPermission {

    /**
     * 接口描述信息
     */
    String description() default "";

    /**
     * 此接口是否为只读, 默认是 true
     */
    boolean readOnly() default true;

    /**
     * 扩展属性
     * 最好以 JSON 格式去存储
     */
    String extra() default "";
}
