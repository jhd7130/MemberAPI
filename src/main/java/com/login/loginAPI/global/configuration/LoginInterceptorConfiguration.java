package com.login.loginAPI.global.configuration;

import com.login.loginAPI.global.security.interceptor.LoginTokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class LoginInterceptorConfiguration implements WebMvcConfigurer {

    private final LoginTokenHandler loginTokenHandler;

    public LoginInterceptorConfiguration(LoginTokenHandler loginTokenHandler){
        this.loginTokenHandler = loginTokenHandler;
    }

    // 로그인과 회원가입으 제외하고 모든 요청에서 토큰 체크할 수 있도록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTokenHandler)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/api/join")
                .excludePathPatterns("/api/newsList")
                .excludePathPatterns("/api/check/*")
                .excludePathPatterns("/api/newsList/*");
    }
}
