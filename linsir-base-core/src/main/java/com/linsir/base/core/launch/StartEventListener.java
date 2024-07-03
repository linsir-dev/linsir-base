package com.linsir.base.core.launch;

import com.linsir.base.core.launch.server.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * @ClassName : StartEventListener
 * @Description : 启动是监听
 * @Author : Linsir
 * @Date: 2023-12-19 16:39
 */

@Slf4j
@Configuration
public class StartEventListener {

    @Async
    @Order
    @EventListener(WebServerInitializedEvent.class)
    public void afterStart(WebServerInitializedEvent event) {
        Environment environment = event.getApplicationContext().getEnvironment();
        String appName = environment.getProperty("spring.application.name").toUpperCase();
        int localPort = event.getWebServer().getPort();
        String profile = StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles());
        String[] beans = event.getApplicationContext().getBeanDefinitionNames();

        Arrays.stream(beans).forEach(bean->{
            log.info("容器注册bean---[{}]",bean);
        });

        log.info("---[{}]---启动完成，当前使用的端口:[{}]，环境变量:[{}]---", appName, localPort, profile);


    }
}
