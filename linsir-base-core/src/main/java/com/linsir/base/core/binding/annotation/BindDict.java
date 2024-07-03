package com.linsir.base.core.binding.annotation;

import java.lang.annotation.*;

/**
 * @author Administrator
 * @title: BindDict
 * @projectName linsir
 * @description: 绑定字典注解
 * @date 2022/3/20 0:02
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BindDict {

    /***
     * 绑定数据字典类型
     * @return
     */
    String type();

    /***
     * 数据字典项取值字段
     * @return
     */
    String field() default "";
}
