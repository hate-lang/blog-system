package com.langtian.config;

import com.langtian.interceptors.LoginInterceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 告诉 Spring，这是一个配置类（人事部文件）
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptors loginInterceptors;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 派保安去站岗！
        registry.addInterceptor(loginInterceptors)
                .addPathPatterns("/**")  // 设下天罗地网：拦截所有请求 (/** 代表无论访问什么网址都拦)
                .excludePathPatterns("/login","/article/public/**","/author/public/**"); // 开绿灯：唯独放过 /login 接口，因为人家还没登录呢！
    }

    //  新增：配置全局跨域通关文牒！
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有接口
                .allowedOriginPatterns("*") // 允许所有前端地址访问（如果你想严格点，可以写 "http://localhost:5173"）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 允许的请求方式
                .allowedHeaders("*") // 允许携带任何请求头
                .allowCredentials(true) // 允许携带前端凭证
                .maxAge(3600); // 问路请求的有效期（秒）
    }
}
