package com.linsir.base.core.binding.parser;

import java.lang.annotation.Annotation;

/**
 * @author: linsir
 * @date: 2022/3/21 14:20
 * @description: 字段名与注解的包装对象关系
 */
public class FieldAnnotation {
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 字段类型
     */
    private Class<?> fieldClass;
    /**
     * 注解
     */
    private Annotation annotation;

    public FieldAnnotation(String fieldName, Class fieldClass, Annotation annotation){
        this.fieldName = fieldName;
        this.fieldClass = fieldClass;
        this.annotation = annotation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Class getFieldClass(){
        return fieldClass;
    }
}
