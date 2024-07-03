package com.linsir.base.core.binding.query.dynamic;

import com.baomidou.mybatisplus.annotation.TableField;
import com.linsir.base.core.binding.parser.ParserCache;
import com.linsir.base.core.binding.query.BindQuery;
import com.linsir.base.core.binding.query.Comparison;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import lombok.Data;

import javax.lang.model.type.NullType;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author: linsir
 * @date: 2022/3/21 14:32
 * @description: BindQuery注解连接器
 */

@Data
public class AnnoJoiner implements Serializable {
    private static final long serialVersionUID = 4886554757194393014L;

    public AnnoJoiner(Field field, BindQuery query){
        this.key = field.getName() + query;
        this.fieldName = field.getName();
        this.comparison = query.comparison();
        // 列名
        if (V.notEmpty(query.field())) {
            this.columnName = S.toSnakeCase(query.field());
        }
        else if (field.isAnnotationPresent(TableField.class)) {
            this.columnName = field.getAnnotation(TableField.class).value();
        }
        if(V.isEmpty(this.columnName)){
            this.columnName = S.toSnakeCase(field.getName());
        }
        // join 表名
        if(!NullType.class.equals(query.entity())){
            this.join = ParserCache.getEntityTableName(query.entity());
        }
        // 条件
        if(V.notEmpty(query.condition())){
            this.condition = query.condition();
        }
    }

    private String key;

    private Comparison comparison;

    private String fieldName;

    private String columnName;

    private String condition;

    private String join;
    /**
     * 别名
     */
    private String alias;
    /**
     * on条件
     */
    private String onSegment;

    /**
     * 中间表
     */
    private String middleTable;

    /**
     * 中间表别名
     */
    public String getMiddleTableAlias(){
        if(middleTable != null && alias != null){
            return alias+"m";
        }
        return null;
    }
    /**
     * 中间表on
     */
    private String middleTableOnSegment;

    /**
     * 解析
     */
    public void parse(){
        // 解析查询
        JoinConditionManager.parseJoinCondition(this);
    }
}
