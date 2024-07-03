package com.linsir.base.core.boot.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @ClassName : LinsirAsyncProperties
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-19 21:08
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties("linsir.async")
public class LinsirAsyncProperties {

    /**
     * 异步核心线程数，默认：2
     */
    private int corePoolSize = 2;
    /**
     * 异步最大线程数，默认：50
     */
    private int maxPoolSize = 50;
    /**
     * 队列容量，默认：10000
     */
    private int queueCapacity = 10000;
    /**
     * 线程存活时间，默认：300
     */
    private int keepAliveSeconds = 300;
}
