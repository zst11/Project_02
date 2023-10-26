package com.briup.cms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//使用@Configuration注解和代码，替代xml文件进行配置
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 跨域配置: 通过跨域过滤器实现
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();

        // 允许跨域的头部信息
        config.addAllowedHeader("*");
        // 允许跨域的方法
        config.addAllowedMethod("*");
        // 可访问的外部域
        config.addAllowedOrigin("*");
        // 需要跨域用户凭证（cookie、HTTP认证及客户端SSL证明等）
        //config.setAllowCredentials(true);
        //config.addAllowedOriginPattern("*");

        // 跨域路径配置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
