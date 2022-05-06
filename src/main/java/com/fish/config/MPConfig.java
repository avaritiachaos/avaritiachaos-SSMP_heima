package com.fish.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MPConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor1 = new MybatisPlusInterceptor();
        mybatisPlusInterceptor1.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor1;
    }
}
