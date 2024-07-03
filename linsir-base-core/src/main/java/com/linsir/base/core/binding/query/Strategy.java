package com.linsir.base.core.binding.query;

/**
 * @author: Administrator
 * @date: 2022/3/21 16:27
 * @description: 查询策略（针对空值等的查询处理策略）
 */
public enum Strategy {

    /**
     * 忽略空字符串""
     */
    IGNORE_EMPTY,
    /**
     * 空字符串""参与查询
     */
    INCLUDE_EMPTY,
}
