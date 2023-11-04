package com.briup.cms.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
    //用法： 当web层中方法需要提供日志记录功能，只需要在该方法添加
    // logging注解即可
    /**
     * 日志描述信息,可用于描述接口的用途
     */
    String value() default "";
}


