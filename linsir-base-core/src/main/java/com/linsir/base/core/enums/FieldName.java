package com.linsir.base.core.enums;

/**
 * @author ：linsir
 * @date ：Created in 2022/6/2 13:26
 * @description：固定字段枚举
 * @modified By：
 * @version: 0.0.1
 */
public enum FieldName {

        /**
         * 主键属性名
         */
        id,
        /**
         * 默认的上级ID属性名
         */
        parentId,
        /**
         * 子节点属性名
         */
        children,
        /**
         * 逻辑删除标记字段
         */
        deleted,
        /**
         * 创建时间字段
         */
        createTime,
        /**
         * 更新时间
         */
        updateTime,
        /**
         * 创建人
         */
        createBy,
        /**
         * 更新人
         */
        updateBy

}
