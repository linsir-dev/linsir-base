package com.linsir.base.core.launch.props;

import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @ClassName : LinsirPropertySource
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-19 19:04
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinsirPropertySource {

    /**
     * Indicate the resource location(s) of the properties file to be loaded.
     * for example, {@code "classpath:/com/example/app.yml"}
     *
     * @return location(s)
     */
    String value();

    /**
     * load app-{activeProfile}.yml
     *
     * @return {boolean}
     */
    boolean loadActiveProfile() default true;

    /**
     * Get the order value of this resource.
     *
     * @return order
     */
    int order() default Ordered.LOWEST_PRECEDENCE;
}
