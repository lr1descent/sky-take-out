package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解AutoFill: 凡是带有该注解的方法在执行前都会自动填充公共字段
 * - Target为注解出现的位置
 * - Retention为注解持久策略，定义为RUNTIME可以在运行的时候被反射读取到
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    OperationType value();
}
