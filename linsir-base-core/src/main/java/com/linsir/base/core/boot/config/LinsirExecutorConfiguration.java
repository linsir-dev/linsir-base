package com.linsir.base.core.boot.config;

import com.linsir.base.core.boot.props.LinsirAsyncProperties;
import lombok.AllArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.linsir.base.core.boot.context.LinsirRunnableWrapper;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName : LinsirExecutorConfiguration
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-19 21:06
 */
@Configuration
@EnableAsync
@EnableScheduling
@AllArgsConstructor
@EnableConfigurationProperties({
        LinsirAsyncProperties.class
})
public class LinsirExecutorConfiguration extends AsyncConfigurerSupport {

    private final LinsirAsyncProperties linsirAsyncProperties;

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(linsirAsyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(linsirAsyncProperties.getMaxPoolSize());
        executor.setQueueCapacity(linsirAsyncProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(linsirAsyncProperties.getKeepAliveSeconds());
        executor.setThreadNamePrefix("async-executor-");
        // 传递线程变量
        executor.setTaskDecorator(LinsirRunnableWrapper::new);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
