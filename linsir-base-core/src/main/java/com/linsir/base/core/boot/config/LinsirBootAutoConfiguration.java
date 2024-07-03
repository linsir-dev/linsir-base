package com.linsir.base.core.boot.config;

import com.linsir.base.core.launch.props.LinsirPropertySource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @ClassName : LinsirBootAutoConfiguration
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-19 20:58
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@LinsirPropertySource(value = "classpath:/linsir-boot.yml")
public class LinsirBootAutoConfiguration {
}
