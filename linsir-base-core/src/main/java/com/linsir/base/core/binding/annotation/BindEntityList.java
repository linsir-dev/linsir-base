package com.linsir.base.core.binding.annotation;

import java.lang.annotation.*;

/**
 * @author Administrator
 * @title: BindEntityList
 * @projectName linsir
 * @description: 绑定Entity集合注解（1-n）
 * @date 2022/3/20 23:09
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BindEntityList {

    /***
     * 对应的entity类
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
