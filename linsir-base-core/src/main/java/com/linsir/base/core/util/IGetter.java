package com.linsir.base.core.util;

import java.io.Serializable;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/21 19:19
 * @description：getter方法接口定义
 * @modified By：
 * @version: 0.0.1
 */
@FunctionalInterface
public interface IGetter<T> extends Serializable {
    Object apply(T source);
}
