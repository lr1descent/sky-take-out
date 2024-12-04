package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自动填充切面，自动填充公共字段，例如: createUser, updateUser, createTime, updateTime
 */
@Aspect
@Slf4j
@Component
public class AutoFillAspect {
    /*
    切面 = 通知 + 切点
    需要定义切点和通知
     */

    /**
     * 切点：com.sky.mapper下面的所有类的所有"@AutoFill"注解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    /**
     * 前置通知：在方法执行前进行自动字段的填充
     * mapper中的方法一旦执行，再填充pojo中的字段就没有意义了
     * 因为此时update或者insert已经执行结束
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        // 此处可以测试发送update或者Insert请求后切面能否执行
        log.info("自动填充公共字段...");

        // 获取注解中的操作类型，是Insert还是Update
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = annotation.value();

        // 获取方法中传过来的对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;

        // 因为是统一自动填充公共字段，所以传过来的对象有可能是Employee，也有可能是Category等
        // 规定要填充公共字段的对象在参数第一位
        // 所以这里要用Object接受args[0]
        Object entity = args[0];

        // 准备填充的公共字段的数据
        LocalDateTime time = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据不同操作类型执行不同公共字段的填充
        if (value == OperationType.INSERT) {
            try {
                // 通过反射获取entity的方法对象
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // invoke方法对象
                setCreateTime.invoke(entity, time);
                setUpdateTime.invoke(entity, time);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (value == OperationType.UPDATE) {
            try {
                // 通过反射获取entity的方法对象
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // invoke方法对象
                setUpdateTime.invoke(entity, time);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
