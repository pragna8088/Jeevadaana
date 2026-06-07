package com.jeevadaana.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(SessionKeys.DONOR_ID, "/donor/login"))
                .addPathPatterns("/donor/**")
                .excludePathPatterns("/donor/login", "/donor/register", "/donor/logout");

        registry.addInterceptor(new AuthInterceptor(SessionKeys.ORGANIZER_ID, "/organizer/login"))
                .addPathPatterns("/organizer/**")
                .excludePathPatterns("/organizer/login", "/organizer/register", "/organizer/logout");
    }
}
