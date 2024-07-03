package com.linsir.base.core.binding.annotation;

import java.lang.annotation.*;

/**
 * @author ：linsir
 * @date ：Created in 2022/9/3 14:54
 * @description： 绑定子项的条目计数
 * @modified By：
 * @version:
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BindCount {

    /***
     * 绑定的Entity类
     * @return
     */
    Class entity();

    /***
     * JOIN连接条件
     * @return
     */
    String condition();
}
