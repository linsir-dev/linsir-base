package com.linsir.base.core.binding.annotation;

import java.lang.annotation.*;

/**
 * @author linsir
 * @title: BindEntity
 * @projectName linsir
 * @description: TODO
 * @date 2022/3/20 1:15
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BindEntity {

    /***
     * 对应的service类
     * @return
     */
    Class entity();

    /***
     * JOIN连接条件
     * @return
     */
    String condition();

    /**
     * 深度绑定
     * @return
     */
    boolean deepBind() default false;
}
