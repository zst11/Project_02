package com.briup.cms.config;

import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfig {
    @Bean
    @ConditionalOnMissingBean
    public Gson gson() {
        return new Gson();
    }
}