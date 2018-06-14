package com.alibaba.spring.web;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;

import java.util.List;

/**
 * Test {@link WebMvcConfigurer}
 *
 * @author @throws Exception execution {@link Exception}
 * @since 1.0.0
 */
public class TestWebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ServletRequestMethodArgumentResolver());
    }

}
