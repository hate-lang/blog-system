package com.langtian.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.langtian.pojo.Result;
import com.langtian.utils.JwtUtils;
import com.langtian.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component  //把保安交给spring老板管理。
public class LoginInterceptors implements HandlerInterceptor {

    //这个方法在请求到达controller层调用 。
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 新增：如果是浏览器发来的 OPTIONS 问路请求，直接放行！
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 1. 保安搜身：从请求头 (Header) 里找一个叫 "Authorization" 的通行证
        String token = request.getHeader("Authorization");
        // 2. 验证通行证
        try {
            // 如果 JwtUtils 验明正身，没有报错，说明是好人！
            Map<String, Object> claims = JwtUtils.parseToken(token);

            // 2. 验证成功！把拿到的用户信息（小纸条）顺手塞进当前线程的储物柜里！
            ThreadLocalUtil.set(claims);
            // 放行！(return true 就代表让请求继续往下走，去 Controller)
            return true;
        } catch (Exception e) {
            // 3. 一旦报错（没带通行证、伪造的、过期的），当场拿下！

            // 设置响应状态码为 401 (代表未授权)
            response.setStatus(401);

            // 告诉前端：你的通行证无效！(因为这里不能直接 return Result，只能用底层方法写回给浏览器)
            response.setContentType("application/json;charset=utf-8");
            Result<String> errorResult = Result.error("未登录或通行证已过期，请重新登录！");

            // 把 Result 对象变成 JSON 字符串写出去

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(errorResult));

            // 拦截！(return false 代表请求到此为止，别想进 Controller)
            return false;
        }
    }

    //新增魔法方法：在这次请求彻底处理完（哪怕报错了）之后，一定会执行这里！
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //擦屁股：清空储物柜，防止内存泄漏和下一个用户的身份串号！
        ThreadLocalUtil.remove();
    }
}
