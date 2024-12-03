package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        // 报错提示：Duplicate entry '2222' for key 'employee.idx_username'
        log.error("异常信息：{}", ex.getMessage());

        // 获取异常信息，对异常信息进行切片，获取重复的用户名
        String[] messages = ex.getMessage().split(" ");
        String userName = messages[2];

        // 定义返回在Result中的异常信息message，使用用户名进行拼接
        String message = userName + MessageConstant.ALREADY_EXIST;

        // 返回包含message的Result
        return Result.error(message);
    }

}
