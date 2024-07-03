package com.linsir.base.core.binding.annotation;

import java.lang.annotation.*;
/**
 * @author: Administrator
 * @date: 2022/3/21 14:23
 * @description:
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BindField {

    /***
     * 绑定的Entity类
     * @return
     */
    Class entity();

    /***
     * 绑定字段
     * @return
     */
    String field();

    /***
     * JOIN连接条件
     * @return
     */
    String condition();
}
