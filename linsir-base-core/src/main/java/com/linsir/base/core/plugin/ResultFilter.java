package com.linsir.base.core.plugin;

/**
 * @ProjectName: linsir
 * @Package: com.linsir.core.plugin
 * @ClassName: ResultFilter
 * @Description: 结果中忽略掉的字段$
 * @Author:Linsir
 * @CreateDate: 2022/9/13 15:07
 * @UpdateDate: 2022/9/13 15:07
 * @Version: 0.0.1$
 */
public interface ResultFilter {

    /**
     * 结果过滤器,需要全局忽略的字段
     * @param data
     * @param <T>
     */
    <T> void filterData(T data);
}
