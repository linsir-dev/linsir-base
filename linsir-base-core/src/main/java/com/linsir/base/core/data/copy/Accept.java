package com.linsir.base.core.data.copy;
import java.lang.annotation.*;
/**
 * @author ：linsir
 * @date ：Created in 2022/3/21 19:17
 * @description：拷贝字段时的非同名字段处理
 * @modified By：
 * @version: 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Accept {
    /**
     * 接收来源对象的属性名
     * @return
     */
    String name();
    /**
     * source该字段有值时是否覆盖
     * @return
     */
    boolean override() default false;
}
