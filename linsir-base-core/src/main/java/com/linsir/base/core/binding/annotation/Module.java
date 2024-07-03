package com.linsir.base.core.binding.annotation;

import java.lang.annotation.*;
/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 10:50
 * @description：模块注解
 * @modified By：
 * @version: 0.0.1
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Module {
    /***
     * 指定模块名
     * @return
     */
    String value();
}
