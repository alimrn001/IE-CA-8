package com.baloot.baloot.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.servlet.Filter;

@Configuration
//@ServletComponentScan
public class FilterConfig {

    // Add filter bean definitions here

    @Bean
    public FilterRegistrationBean<CORSFilter> corsFilter() {
        FilterRegistrationBean<CORSFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new CORSFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

//    @Bean
//    public FilterRegistrationBean<JWTAuthFilter> jwtAuthFilter() {
//        FilterRegistrationBean<JWTAuthFilter> registrationBean = new FilterRegistrationBean<>();
//
//        registrationBean.setFilter(new JWTAuthFilter());
//        registrationBean.addUrlPatterns("/*");
//
//        return registrationBean;
//    }

}
