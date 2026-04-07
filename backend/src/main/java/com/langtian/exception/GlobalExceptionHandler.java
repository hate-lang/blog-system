package com.langtian.exception;

import com.langtian.pojo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice  //  神奇的注解：告诉 Spring 这是一个“拦截所有 Controller 的总管家”
public class GlobalExceptionHandler {

    // 告诉管家：只要是代码里抛出了 Exception (所有异常的老祖宗)，就全部拦截到这里处理！
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e){

        // 1. 在后台控制台打印出红色的报错堆栈，方便咱们程序员自己排错（这句不能省！）
        e.printStackTrace();

        // 2. 优雅地包装成 Result 返回给前端
        // (判断一下异常里有没有带错误信息，如果有就返回错误信息，如果没有就给个默认提示)
        String message = StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "服务器内部发生未知错误，请联系管理员！";

        return Result.error(message);
    }
}
