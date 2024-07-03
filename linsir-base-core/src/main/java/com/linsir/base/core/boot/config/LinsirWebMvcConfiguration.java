package com.linsir.base.core.boot.config;

import com.linsir.base.core.boot.props.LinsirFileProperties;
import com.linsir.base.core.boot.props.LinsirUploadProperties;
import com.linsir.base.core.boot.resolver.TokenArgumentResolver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.util.List;

/**
 * @ClassName : LinsirWebMvcConfiguration
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-19 22:24
 */

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@EnableConfigurationProperties({
        LinsirUploadProperties.class, LinsirFileProperties.class
})
public class LinsirWebMvcConfiguration implements WebMvcConfigurer {

    private final LinsirUploadProperties linsirUploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = linsirUploadProperties.getSavePath();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + path + "/upload/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new TokenArgumentResolver());
    }
}
