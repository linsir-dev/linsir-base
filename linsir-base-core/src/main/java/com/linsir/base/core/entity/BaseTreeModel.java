package com.linsir.base.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linsir.base.core.constant.CommonConstant;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;

import java.io.Serializable;

/**
 * @author linsir
 * @version 1.0.0
 * @title BaseTreeModel
 * @description
 * @create 2024/7/6 13:34
 */


public class BaseTreeModel<U> extends AbstractEntity<Long>{

    private U parentId;

    public BaseTreeModel<U> setParentId(U parentId) {
        this.parentId = parentId;
        return this;
    }

    public U getParentId() {
        return this.parentId;
    }


    /**
     * 父级ID的全路径
     * <p>
     * 格式：/([0-9a-f]+,)+/g
     */
    @JsonIgnore
    private String parentIdsPath;

    public BaseTreeModel<U> setParentIdsPath(String parentIdsPath) {
        if (V.notEmpty(parentIdsPath) && !S.endsWith(parentIdsPath, CommonConstant.SEPARATOR_COMMA)) {
            parentIdsPath += CommonConstant.SEPARATOR_COMMA;
        }
        this.parentIdsPath = parentIdsPath;
        return this;
    }
}
