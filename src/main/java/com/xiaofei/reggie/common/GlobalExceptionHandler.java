package com.xiaofei.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            String[] str = ex.getMessage().split(" ");
            String msg = str[2] + "已存在";

            return R.error(msg);
        }

        return R.error("添加失败了");
    }

    /**
     * 异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public R<String> exceptionHandler(RuntimeException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
