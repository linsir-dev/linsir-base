package com.linsir.base.core.vo;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/22 11:34
 * @description：LabelValue键值对形式的VO（用于构建显示名Name-存储值Value形式的结果）
 * @modified By：
 * @version: 0.0.1
 */
@Data
public class LabelValue implements Serializable {
    private static final long serialVersionUID = -1230553268907478070L;

    public LabelValue() {
    }

    public LabelValue(String label, Object value) {
        this.value = value;
        this.label = label;
    }

    /**
     * 对象类型
     */
    private String type;

    /**
     * label: 显示值
     */
    private String label;

    /**
     * value: 存储值
     */
    private Object value;

    /**
     * 扩展值
     */
    private Object ext;

    /**
     * 是否为叶子节点
     */
    private Boolean leaf;

    /**
     * 是否禁用；非异步加载时，非叶子节点且无叶子节点时会自动禁用该节点
     */
    private Boolean disabled;

    /**
     * 子节点集合
     */
    private List<LabelValue> children;

    @JsonGetter("isLeaf")
    public Boolean isLeaf() {
        return leaf;
    }
}
