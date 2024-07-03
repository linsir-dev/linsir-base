package com.linsir.base.core.plugin;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 23:46
 * @description：JsonResult结果过滤器
 * @modified By：
 * @version:
 */
public interface JsonResultFilter {

    /**
     * 需要全局忽略的字段
     * @return
     */
    <T> void filterData(T data);

}
