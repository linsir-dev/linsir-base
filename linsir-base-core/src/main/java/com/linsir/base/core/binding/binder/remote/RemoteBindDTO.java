package com.linsir.base.core.binding.binder.remote;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 15:48
 * @description：远程绑定DTO定义
 * @modified By：
 * @version: 0.0.1
 */
@Data
@Accessors(chain = true)
public class RemoteBindDTO implements Serializable {
    private static final long serialVersionUID = -6089227319362675819L;

    private String entityClassName;
    private String[] selectColumns;
    private String refJoinCol;
    private Collection<?> inConditionValues;
    private List<String> additionalConditions;
    private String orderBy;
    private String resultType;

    public RemoteBindDTO() {
    }

    public RemoteBindDTO(Class<?> entityClass) {
        this.entityClassName = entityClass.getName();
    }
}
