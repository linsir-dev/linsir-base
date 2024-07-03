package com.linsir.base.core.util;

import java.io.Serializable;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/21 19:20
 * @description：setter方法接口定义
 * @modified By：
 * @version: 0.0.1
 */
@FunctionalInterface
public interface ISetter<T, U> extends Serializable {
    void accept(T t, U u);
}