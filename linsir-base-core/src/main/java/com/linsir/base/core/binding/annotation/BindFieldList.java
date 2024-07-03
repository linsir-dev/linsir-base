package com.linsir.base.core.binding.annotation;

import java.lang.annotation.*;

/**
 * @author: Administrator
 * @date: 2022/3/21 14:24
 * @description: 绑定字段集合（1-n）
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BindFieldList {

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

    /**
     * EntityList排序，示例 `id:DESC,age:ASC`
     * @return
     */
    String orderBy() default "";

    /**
     * 分隔符，用于拆解拼接存储的多个id值
     * @return
     */
    String splitBy() default "";
}
