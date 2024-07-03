package com.linsir.base.core.binding.parser;

import com.linsir.base.core.binding.query.Comparison;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author ：linsir
 * @date ：Created in 2022/9/4 1:35
 * @description：字段
 * @modified By：
 * @version:
 */
@Data
@Accessors(chain = true)
public class FieldComparison implements Serializable {
    private static final long serialVersionUID = -1080962768714815036L;

    private String fieldName;

    private Comparison comparison;

    private Object value;

    public FieldComparison(){}

    public FieldComparison(String fieldName, Comparison comparison, Object value) {
        this.fieldName = fieldName;
        this.comparison = comparison;
        this.value = value;
    }

}
